package fr.cel.gameapi.manager.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.cel.gameapi.GameAPI;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class NPC {

    // https://github.com/Circuit-board/DecorativeNPCs/blob/main/src/main/java/cool/circuit/decorativeNPCS/NPC.java#L297

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

        ServerLevel level = ((CraftWorld) this.location.getWorld()).getHandle();

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile = new GameProfile(this.uuid, this.displayName);
        this.npc = new ServerPlayer(server, level, gameProfile, ClientInformation.createDefault());

        if (this.skin.value().isEmpty() || this.skin.signature().isEmpty()) {
            Skin skin = SkinFetcher.fetchSkin(this.displayName);
            if (skin != null) {
                gameProfile.getProperties().put("textures", new Property("textures", skin.value(), skin.signature()));
            }
        } else {
            gameProfile.getProperties().put("textures", new Property("textures", this.skin.value(), this.skin.signature()));
        }
    }

    /**
     * Spawns the NPC for a specific player.
     *
     * @param player The player to spawn the NPC for.
     */
    public void spawn(Player player) {
        if (!this.location.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) return;

        // Location
        this.npc.setPos(this.location.getX(), this.location.getY(), this.location.getZ());
        this.npc.setYHeadRot(this.location.getYaw());
        this.npc.setYBodyRot(this.location.getYaw());
        this.npc.setYRot(this.location.getYaw());
        this.npc.setXRot(this.location.getPitch());

        // Skin Customization
        SynchedEntityData synchedEntityData = this.npc.getEntityData();
        synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);

        // Latency
        if(((CraftPlayer) player).getHandle().connection == null) {
            Bukkit.getScheduler().runTaskLater(GameAPI.getInstance(), () -> setValue(this.npc, "f", ((CraftPlayer) player).getHandle().connection),30L);
        } else {
            setValue(this.npc, "f", ((CraftPlayer) player).getHandle().connection);
        }

        // Add Player
        sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this.npc), player);
        ServerEntity serverEntity = new ServerEntity(this.npc.serverLevel(), npc, 0, false, packet -> {}, Set.of());
        Packet<?> packet = this.npc.getAddEntityPacket(serverEntity);
        sendPacket(packet, player);
        sendPacket(new ClientboundSetEntityDataPacket(this.npc.getId(), synchedEntityData.getNonDefaultValues()), player);

        // Retirer le NPC de la tablist après l’avoir affiché
        Bukkit.getScheduler().runTaskLater(GameAPI.getInstance(), () -> {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            ClientboundPlayerInfoRemovePacket removePacket = new ClientboundPlayerInfoRemovePacket(List.of(this.uuid));
            serverPlayer.connection.send(removePacket);
        }, 20L);

        // Pose
        npc.setPose(pose);
        SynchedEntityData dataWatcher = npc.getEntityData();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendPacket(new ClientboundSetEntityDataPacket(npc.getId(), dataWatcher.getNonDefaultValues()), onlinePlayer);
        }
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
        this.npc.setYHeadRot(this.location.getYaw());
        this.npc.setYBodyRot(this.location.getYaw());
        this.npc.setYRot(this.location.getYaw());
        this.npc.setXRot(this.location.getPitch());
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

        // remplace this.npc.setRot(float yaw, float pitch) qui est en protected
        this.setRot(location.getYaw(), location.getPitch());

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

    private void setRot(float f, float f1) {
        if (Float.isNaN(f)) {
            f = 0.0F;
        }

        if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
            f = 0.0F;
        }

        if (Float.isNaN(f1)) {
            f1 = 0.0F;
        }

        if (f1 == Float.POSITIVE_INFINITY || f1 == Float.NEGATIVE_INFINITY) {
            f1 = 0.0F;
        }

        this.npc.setYRot(f % 360.0F);
        this.npc.setXRot(f1 % 360.0F);
    }

    private void sendPacket(Packet<?> packet, Player player) {
        // Ensure the player has a valid connection before sending the packet
        if (((CraftPlayer) player).getHandle().connection == null) {
            Bukkit.getScheduler().runTaskLater(GameAPI.getInstance(), () -> {
                // Double-check if the player's connection is available before sending the packet
                if (((CraftPlayer) player).getHandle().connection != null) {
                    ((CraftPlayer) player).getHandle().connection.sendPacket(packet);
                }
            }, 25L);
        } else {
            // If the connection is already available, send the packet immediately
            ((CraftPlayer) player).getHandle().connection.sendPacket(packet);
        }
    }

    private void setValue(Object packet, String fieldName, Object value) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(packet, value);
        } catch (Exception e) {
            GameAPI.getInstance().getLogger().severe("Failed to set value for field " + fieldName + " in packet " + packet.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

}