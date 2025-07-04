package fr.cel.gameapi.manager.npc;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.minecraft.Optionull;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

@Getter
public class NPC {

    private final UUID uuid;
    private final String name;
    private final String displayName;
    private final Skin skin;

    private ServerPlayer npc;
    private Location location;
    private Pose pose;

    /**
     * Constructs a new NPC with the specified parameters.
     *
     * @param name        The name of the NPC.
     * @param displayName The display name of the NPC.
     * @param location    The location where the NPC will be spawned.
     * @param skin        The skin of the NPC.
     */
    public NPC(String name, String displayName, Location location, Skin skin) {
        this(name, displayName, location, skin, Pose.STANDING);
    }

    /**
     * Constructs a new NPC with the specified parameters.
     *
     * @param name        The name of the NPC.
     * @param displayName The display name of the NPC.
     * @param location    The location where the NPC will be spawned.
     * @param skin        The skin of the NPC.
     * @param pose        The pose of the NPC
     */
    public NPC(String name, String displayName, Location location, Skin skin, Pose pose) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.displayName = displayName;
        this.location = location;
        this.skin = skin;
        this.pose = pose;
    }

    /**
     * Creates the NPC and initializes its properties.
     * This method should be called before spawning the NPC.
     */
    public void create() {
        if (npc != null) return;

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel level = ((CraftWorld) this.location.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(this.uuid, this.displayName);

        this.npc = new ServerPlayer(server, level, new GameProfile(uuid, ""), ClientInformation.createDefault());
        npc.gameProfile = gameProfile;
    }

    /**
     * Spawns the NPC for a specific player.
     *
     * @param player The player to spawn the NPC for.
     */
    public void spawn(Player player) {
        if (!this.location.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) return;

        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        if (this.skin.value().isEmpty() || this.skin.signature().isEmpty()) {
            Skin skin = SkinFetcher.fetchSkin(this.displayName);
            if (skin != null) {
                npc.getGameProfile().getProperties().replaceValues(
                        "textures",
                        ImmutableList.of(new Property("textures", skin.value(), skin.signature()))
                );
            }
        } else {
            npc.getGameProfile().getProperties().replaceValues(
                    "textures",
                    ImmutableList.of(new Property("textures", this.skin.value(), this.skin.signature()))
            );
        }

        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.Action.class);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);

        ClientboundPlayerInfoUpdatePacket playerInfoPacket = new ClientboundPlayerInfoUpdatePacket(actions, getEntry(npc, serverPlayer));
        serverPlayer.connection.send(playerInfoPacket);

        npc.setPos(getLocation().x(), getLocation().y(), getLocation().z());

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                npc.getId(),
                npc.getUUID(),
                getLocation().x(),
                getLocation().y(),
                getLocation().z(),
                getLocation().getPitch(),
                getLocation().getYaw(),
                npc.getType(),
                0,
                Vec3.ZERO,
                getLocation().getYaw()
        );
        serverPlayer.connection.send(addEntityPacket);

        setPose(pose);

        ClientboundPlayerInfoRemovePacket playerInfoRemovePacket = new ClientboundPlayerInfoRemovePacket(List.of(npc.getUUID()));
        serverPlayer.connection.send(playerInfoRemovePacket);

        update(player);
    }

    public void update(Player player) {
        if (npc == null) return;

        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        npc.getEntityData().set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));

        refreshEntityData(player);
    }

    private void refreshEntityData(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        SynchedEntityData.DataItem<?>[] itemsById = (SynchedEntityData.DataItem<?>[]) getValue(npc.getEntityData(), "itemsById"); // itemsById
        List<SynchedEntityData.DataValue<?>> entityData = new ArrayList<>();
        for (SynchedEntityData.DataItem<?> dataItem : itemsById) {
            entityData.add(dataItem.value());
        }
        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(npc.getId(), entityData);
        serverPlayer.connection.send(setEntityDataPacket);
    }

    /**
     * Spawns the NPC for all online players.
     */
    public void showToAll() {
        Bukkit.getOnlinePlayers().forEach(this::spawn);
    }

    /**
     * Spawns the NPC for a specific player.
     *
     * @param players The player uuids to spawn the NPC for.
     */
    public void showToAll(List<UUID> players) {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) spawn(player);
        }
    }

    /**
     * Removes the NPC from a specific player.
     *
     * @param player The player to remove the NPC from.
     */
    public void remove(Player player) {
        if (this.npc == null) return;

        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        ClientboundPlayerInfoRemovePacket playerInfoRemovePacket = new ClientboundPlayerInfoRemovePacket(List.of(this.uuid));
        serverPlayer.connection.send(playerInfoRemovePacket);

        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(this.npc.getId());
        serverPlayer.connection.send(removeEntitiesPacket);
    }

    /**
     * Removes the NPC from all online players.
     */
    public void removeToAll() {
        Bukkit.getOnlinePlayers().forEach(this::remove);
    }

    /**
     * Removes the NPC from a list of players.
     *
     * @param players The list of player UUIDs to remove the NPC from.
     */
    public void removeToAll(List<UUID> players) {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) remove(player);
        }
    }

    /**
     * Interacts with the NPC.
     * This method can be overridden to define custom interaction behavior.
     */
    public void interact(Player player) {
        chat("Salut " + player.getName() + " ! Amuses-toi bien sur GDLCA Minigames !", player);
    }

    /**
     * Sets the location of the NPC and updates it for all online players.
     *
     * @param location The new location to set for the NPC.
     */
    public void setLocation(Location location) {
        if (npc == null) return;
        if (location == null) return;

        this.location = location;

        this.npc.setPos(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Sets the pose of the NPC and updates it for all online players.
     *
     * @param pose The new pose to set for the NPC.
     */
    public void setPose(Pose pose) {
        if (npc == null) return;
        if (pose == null) return;

        npc.setPose(pose);

        SynchedEntityData dataWatcher = npc.getEntityData();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendPacket(new ClientboundSetEntityDataPacket(npc.getId(), dataWatcher.getNonDefaultValues()), onlinePlayer);
        }

        this.pose = pose;
    }

    /**
     * Sends a chat message to all online players with the NPC's display name.
     *
     * @param message The message to send.
     */
    public void chat(String message) {
        ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(Component.literal(ChatColor.GOLD + "[" + displayName + "] " + ChatColor.RESET + message), false);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendPacket(packet, onlinePlayer);
        }
    }

    /**
     * Sends a chat message to a specific player with the NPC's display name.
     *
     * @param message The message to send.
     * @param player  The player to send the message to.
     */
    public void chat(String message, Player player) {
        ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(Component.literal(ChatColor.GOLD + "[" + displayName + "] " + ChatColor.RESET + message), false);
        sendPacket(packet, player);
    }

    /**
     * Sends a chat message to a list of players with the NPC's display name.
     *
     * @param message      The message to send.
     * @param uuidPlayers  The list of player UUIDs to send the message to.
     */
    public void chat(String message, List<UUID> uuidPlayers) {
        ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(Component.literal(ChatColor.GOLD + "[" + displayName + "] " + message), false);

        for (UUID uuid : uuidPlayers) {
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer == null) continue;
            sendPacket(packet, onlinePlayer);
        }
    }

    public void lookAt(Player player, Location location) {
        if (this.npc == null) return;

        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        npc.setRot(location.getYaw(), location.getPitch());

        this.npc.setYHeadRot(location.getYaw());
        this.npc.setXRot(location.getPitch());
        this.npc.setYRot(location.getYaw());

        ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(
                this.npc.getId(),
                new PositionMoveRotation(
                        new Vec3(getLocation().getX(), getLocation().getY(), getLocation().getZ()),
                        Vec3.ZERO,
                        location.getYaw(),
                        location.getPitch()),
                Set.of(),
                false
        );
        serverPlayer.connection.send(teleportEntityPacket);

        float angelMultiplier = 256.0F / 360.0F;
        ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(this.npc, (byte) (location.getYaw() * angelMultiplier));
        serverPlayer.connection.send(rotateHeadPacket);
    }

    private void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    private ClientboundPlayerInfoUpdatePacket.Entry getEntry(ServerPlayer npcPlayer, ServerPlayer viewer) {
        GameProfile profile = npcPlayer.getGameProfile();
        return new ClientboundPlayerInfoUpdatePacket.Entry(
                npcPlayer.getUUID(),
                profile,
                false,
                69,
                npcPlayer.gameMode.getGameModeForPlayer(),
                npcPlayer.getTabListDisplayName(),
                true,
                -1,
                Optionull.map(npcPlayer.getChatSession(), RemoteChatSession::asData)
        );
    }

    private Object getValue(Object instance, String name) {
        Object result = null;

        try {
            Field field = instance.getClass().getDeclaredField(name);

            field.setAccessible(true);
            result = field.get(instance);
            field.setAccessible(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}