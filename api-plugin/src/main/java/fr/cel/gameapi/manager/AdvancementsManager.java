package fr.cel.gameapi.manager;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AdvancementsManager {

    /**
     * Gives an advancement to a player. Sends the message only to players in the given UUID list.
     * @param player The player instance to give the advancement to
     * @param advancement The identifier of the advancement to give
     * @param uuids UUIDs of players to show the advancement message to
     */
    public void giveAdvancement(@NotNull Player player, @NotNull Advancements advancement, List<UUID> uuids) {
        NamespacedKey key = NamespacedKey.fromString("gdlca:" + advancement.getCategory() + "/" + advancement.name().toLowerCase());
        if (key == null) return;

        Advancement a = Bukkit.getAdvancement(key);
        if (a == null) return;

        AdvancementProgress progress = player.getAdvancementProgress(a);
        if (!progress.isDone()) {
            for (String string : progress.getRemainingCriteria()) {
                progress.awardCriteria(string);
            }

            if (uuids == null) return;

            for (UUID uuid : uuids) {
                Player pl = Bukkit.getPlayer(uuid);
                if (pl != null) pl.sendMessage(Component.text(player.getName() + " a obtenu le succès ")
                        .append(Objects.requireNonNull(a.getDisplay()).title()));
            }
        }
    }

    /**
     * Gives an advancement to a player
     * @param player The player instance to give the advancement to
     * @param advancement The identifier of the advancement to give
     */
    public void giveAdvancement(@NotNull Player player, @NotNull Advancements advancement) {
        giveAdvancement(player, advancement, null);
    }

    /**
     * Removes an advancement from a player
     * @param player The player instance to remove the advancement from
     * @param advancement The identifier of the advancement to remove
     */
    public void removeAdvancement(@NotNull Player player, @NotNull Advancements advancement) {
        NamespacedKey key = NamespacedKey.fromString("gdlca:" + advancement.getCategory() + "/" + advancement.name().toLowerCase());
        if (key == null) return;

        Advancement a = Bukkit.getAdvancement(key);
        if (a == null) return;

        AdvancementProgress progress = player.getAdvancementProgress(a);
        if (progress.isDone()) {
            for (String string : progress.getRemainingCriteria()) {
                progress.awardCriteria(string);
            }
        }
    }

    @Getter
    public enum Advancements {
        // Cache-Cache
        ROOT("cachecache"),
        AUDACIEUX("cachecache"),
        COLLECTION_PERSONNELLE("cachecache"),
        PIQUE_NIQUE("cachecache"),
        MENAGE_NUISIBLES("cachecache"),
        PIED_POUVOIR("cachecache"),
        MIAOU("cachecache"),
        MONTAGNE_SABLE("cachecache"),
        PAS_BESOIN("cachecache"),
        RAID_CHATEAU("cachecache"),
        RAT_LABORATOIRE("cachecache"),
        TOUJOURS_VIVANT("cachecache"),
        TRAVERSEE_MUSICALE("cachecache"),
        AIMEZ_FAIRE_MAL("cachecache"),
        ;

        private final String category;

        Advancements(@NotNull String category) {
            this.category = category;
        }
    }

}
