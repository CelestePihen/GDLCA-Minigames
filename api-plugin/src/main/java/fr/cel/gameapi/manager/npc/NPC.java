package fr.cel.gameapi.manager.npc;

import com.destroystokyo.paper.SkinParts;
import fr.cel.gameapi.GameAPI;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class NPC {

    @NotNull @Getter private final String name;
    @Nullable @Getter private final Component displayName;

    @Getter private UUID uuid;
    @Getter private Location location;
    private Pose pose = Pose.STANDING;

    @Nullable private Skin skin = null;

    @Getter private Mannequin mannequin;

    /**
     * Creates a new NPC with a display name and location.
     * @param displayName The display name of the NPC.
     * @param location The location where the NPC will be spawned.
     */
    public NPC(@NotNull String name, @Nullable Component displayName, @NotNull Location location) {
        this.name = name;
        this.displayName = displayName;
        this.location = location;
    }

    /**
     * Creates a new NPC with a display name, location, and pose.
     * @param displayName The display name of the NPC.
     * @param location The location where the NPC will be spawned.
     * @param pose The pose of the NPC.
     */
    public NPC(@NotNull String name, @NotNull Component displayName, @NotNull Location location, @NotNull Pose pose) {
        this(name, displayName, location);
        this.pose = pose;
    }

    /**
     * Creates a new NPC with a custom profile.
     * @param displayName The display name of the NPC.
     * @param location The location where the NPC will be spawned.
     * @param pose The pose of the NPC.
     * @param skin The skin of the NPC.
     */
    public NPC(@NotNull String name, @NotNull Component displayName, @NotNull Location location, @NotNull Pose pose, @NotNull Skin skin) {
        this(name, displayName, location, pose);
        this.skin = skin;
    }

    /**
     * Spawns the NPC at its location.
     */
    public void spawn() {
        this.mannequin = location.getWorld().spawn(location, Mannequin.class, mannequin -> {
            this.uuid = mannequin.getUniqueId();

            if (this.displayName == null) {
                mannequin.customName(Component.empty());
                mannequin.setCustomNameVisible(false);
            } else {
                mannequin.customName(this.displayName);
                mannequin.setCustomNameVisible(true);
            }

            mannequin.setPose(pose);
            mannequin.setImmovable(true);
            mannequin.setDescription(null);

            if (skin == null) {
                mannequin.setProfile(ResolvableProfile.resolvableProfile(Bukkit.createProfile(this.name)));
            } else {
                mannequin.setProfile(ResolvableProfile.resolvableProfile().uuid(this.uuid).name(this.name).skinPatch(skinPatchBuilder -> {
                    skinPatchBuilder.body(this.skin.body());
                    skinPatchBuilder.cape(this.skin.cape());
                    skinPatchBuilder.elytra(this.skin.elytra());
                    skinPatchBuilder.model(this.skin.model());
                }).build());
            }

            SkinParts.Mutable mutable = SkinParts.allParts().mutableCopy();
            mutable.setCapeEnabled(false);
            mannequin.setSkinParts(mutable.immutableCopy());
        });
    }

    /**
     * Despawns the NPC.
     */
    public void despawn() {
        if (this.mannequin != null && this.mannequin.isValid()) {
            this.mannequin.remove();
            this.mannequin = null;
        }
    }

    /**
     * Interacts with the NPC.
     * This method can be overridden to define custom interaction behavior.
     */
    public void interact(Player player) {
        chat(Component.text("Salut " + player.getName() + " ! Amuses-toi bien sur GDLCA Minigames !"), player);
    }

    /**
     * Sends a chat message to all online players from the NPC.
     * @param message The message to send.
     */
    public void chat(Component message) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) chat(message, onlinePlayer);
    }

    /**
     * Sends a chat message to a list of players from the NPC.
     * @param message The message to send.
     * @param playerUUIDs The list of player UUIDs to send the message to.
     */
    public void chat(Component message, List<UUID> playerUUIDs) {
        for (UUID uuid : playerUUIDs) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) chat(message, player);
        }
    }

    /**
     * Sends a chat message to a specific player from the NPC.
     * @param message The message to send.
     * @param player The player to send the message to.
     */
    public void chat(Component message, Player player) {
        player.sendMessage(Component.text("[", NamedTextColor.GOLD).append(displayName).append(Component.text("] ")
                .append(message.color(NamedTextColor.WHITE))));
    }

    /**
     * Hides the NPC from a specific player.
     * @param player The player from whom to hide the NPC.
     */
    public void hidePlayer(@NotNull Player player) {
        player.hideEntity(GameAPI.getInstance(), this.mannequin);
    }

    /**
     * Hides the NPC from all online players.
     */
    public void hidePlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) hidePlayer(player);
    }

    /**
     * Hides the NPC from a list of players.
     * @param playerUUIDs The list of player UUIDs from whom to hide the NPC.
     */
    public void hidePlayer(List<UUID> playerUUIDs) {
        for (UUID uuid : playerUUIDs) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) hidePlayer(player);
        }
    }

    /**
     * Shows the NPC to a specific player.
     * @param player The player to whom to show the NPC.
     */
    public void showPlayer(@NotNull Player player) {
        player.showEntity(GameAPI.getInstance(), this.mannequin);
    }

    /**
     * Shows the NPC to all online players.
     */
    public void showPlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) showPlayer(player);
    }

    /**
     * Shows the NPC to a list of players.
     * @param playerUUIDs The list of player UUIDs to whom to show the NPC.
     */
    public void showPlayer(List<UUID> playerUUIDs) {
        for (UUID uuid : playerUUIDs) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) showPlayer(player);
        }
    }

    /**
     * Sets the location of the NPC.
     * @param location The new location of the NPC.
     */
    public void setLocation(@NotNull Location location) {
        this.location = location;
        this.mannequin.teleportAsync(location);
    }

    /**
     * Sets the pose of the NPC.
     * @param pose The new pose of the NPC.
     */
    public void setPose(@NotNull Pose pose) {
        this.pose = pose;
        this.mannequin.setPose(pose);
    }

}