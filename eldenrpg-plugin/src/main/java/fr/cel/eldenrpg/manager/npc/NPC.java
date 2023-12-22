package fr.cel.eldenrpg.manager.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.player.ERPlayer;
import fr.cel.eldenrpg.manager.quest.Quest;
import fr.cel.eldenrpg.utils.ChatUtility;
import fr.cel.eldenrpg.utils.Reflection;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
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
    @Getter private final String displayName;

    @Getter private final Location location;

    @Getter private ServerPlayer npc;
    private final String texture;
    private final String signature;

    @Getter private final boolean isDead;

    protected EldenRPG main;

    @Getter @Setter private Quest quest;

    public NPC(String name, String displayName, Location location, String texture, String signature, boolean isDead, EldenRPG main) {
        this.name = name;
        this.displayName = displayName;
        this.location = location;

        this.texture = texture;
        this.signature = signature;

        this.isDead = isDead;

        this.quest = null;
        this.main = main;
    }

    public void create() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), this.displayName);
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        npc = new ServerPlayer(server, serverLevel, gameProfile);
    }

    public void spawn(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ServerGamePacketListenerImpl ps = serverPlayer.connection;

        if (!location.getWorld().getName().equalsIgnoreCase(serverPlayer.level().getWorld().getName())) {
            return;
        }

        // PlayerInfoUpdatePacket
        ps.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this.npc));

        this.npc.setPos(location.getX(), location.getY(), location.getZ());

        if (this.isDead) this.npc.setPose(Pose.SLEEPING);

        // Spawn Packet
        ps.send(new ClientboundAddPlayerPacket(this.npc));

        if (this.isDead) {
            PlayerTeam team = new PlayerTeam(new Scoreboard(), this.name);

            team.getPlayers().add(this.getDisplayName());
            team.setNameTagVisibility(Team.Visibility.NEVER);
            team.setCollisionRule(Team.CollisionRule.NEVER);

            ps.send(ClientboundSetPlayerTeamPacket.createRemovePacket(team));
            ps.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
        }

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

    public void interact(Player player, ERPlayer erPlayer) {
        if (quest == null) sendMessageWithName(player, "PLACEHOLDER - J'espère que vous éradiquerez le mal sur ces terres, aventurier.");
    }

    protected void sendMessageWithName(Player player, String message) {
        player.sendMessage("§6[NPC] §r" + getDisplayName() + " : " + message);
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

        Int2ObjectMap<SynchedEntityData.DataItem<?>> itemsById = (Int2ObjectMap<SynchedEntityData.DataItem<?>>) Reflection.getField(this.npc.getEntityData(), "e"); // itemsById
        List<SynchedEntityData.DataValue<?>> entityData = new ArrayList<>();
        for (SynchedEntityData.DataItem<?> dataItem : itemsById.values()) {
            entityData.add(dataItem.value());
        }
        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(this.npc.getId(), entityData);
        serverPlayer.connection.send(setEntityDataPacket);
    }

    public int getId() {
        return (this.npc == null) ? 0 : this.npc.getId();
    }

}