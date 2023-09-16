package fr.cel.hub.manager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.cel.hub.utils.Reflection;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {
    
    @Getter private final String name;

    @Getter private final Location location;

    @Getter private ServerPlayer npc;

    private final String texture;
    private final String signature;

    public NPC(String name, Location location, String texture, String signature) {
        this.location = location;
        this.name = name;
        this.texture = texture;
        this.signature = signature;
    }

    public void create() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), this.name);
        
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        npc = new ServerPlayer(server, serverLevel, gameProfile);
    }

    public void spawn(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ServerGamePacketListenerImpl ps = serverPlayer.connection;

        if (!location.getWorld().getName().equalsIgnoreCase(serverPlayer.level().getWorld().getName())) return;

        // PlayerInfoUpdatePacket
        ps.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));

        npc.setPos(location.getX(), location.getY(), location.getZ());

        // Spawn Packet
        ps.send(new ClientboundAddPlayerPacket(npc));
        update(player);
    }

    public void hide(Player player) {
        ServerGamePacketListenerImpl ps = ((CraftPlayer) player).getHandle().connection;

        ps.send(new ClientboundPlayerInfoRemovePacket(List.of(npc.getUUID())));
        ps.send(new ClientboundRemoveEntitiesPacket(npc.getId()));
    }

    public void lookAt(Player player, Location location) {
       ServerGamePacketListenerImpl ps = ((CraftPlayer) player).getHandle().connection;

       try {
           // a est le nom de setRot() avec les remaps
           Method setRotMethod = net.minecraft.world.entity.Entity.class.getDeclaredMethod("a", float.class, float.class);
           setRotMethod.setAccessible(true);
           setRotMethod.invoke(npc, location.getYaw(), location.getPitch());
           setRotMethod.setAccessible(false);
       } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
           e.printStackTrace();
       }
       npc.setYHeadRot(location.getYaw());
       npc.setXRot(location.getPitch());
       npc.setYRot(location.getYaw());

       ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(npc);
       ps.send(teleportEntityPacket);

       float multiplier = 256f / 360f;
       ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(npc, (byte) (location.getYaw() * multiplier));
       ps.send(rotateHeadPacket);
    }

    public void showToAll() {
        Bukkit.getOnlinePlayers().forEach(this::spawn);
    }

    public void hideToAll() {
        Bukkit.getOnlinePlayers().forEach(this::hide);
    }

    private void update(Player player) {
        try {
            // bL est le nom de DATA_PLAYER_MODE_CUSTOMISATION avec les remaps
            Field field = Reflection.getField(net.minecraft.world.entity.player.Player.class, "bL");
            field.setAccessible(true);
            npc.getEntityData().set((EntityDataAccessor<Byte>) field.get(null), (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
            refreshEntityData(player);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    private void refreshEntityData(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        Int2ObjectMap<SynchedEntityData.DataItem<?>> itemsById = (Int2ObjectMap<SynchedEntityData.DataItem<?>>) Reflection.getField(npc.getEntityData(), "e"); // itemsById
        List<SynchedEntityData.DataValue<?>> entityData = new ArrayList<>();
        for (SynchedEntityData.DataItem<?> dataItem : itemsById.values()) {
            entityData.add(dataItem.value());
        }
        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(npc.getId(), entityData);
        serverPlayer.connection.send(setEntityDataPacket);
    }

}