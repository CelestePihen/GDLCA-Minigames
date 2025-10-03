package fr.cel.gameapi.manager;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class AdvancementsManager {

    /**
     * Donne un succès au joueur. N'envoye le message qu'aux joueurs dans la liste uuids.
     * @param player          L'instance du joueur à qui vous voulez donner le succès
     * @param advancement     L'identifiant du succès à donner
     * @param uuids UUIDs des joueurs à qui montrer le succès
     */
    public void giveAdvancement(Player player, Advancements advancement, List<UUID> uuids) {
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
                        .append(a.getDisplay().title()));
            }
        }
    }

    /**
     * Donne un succès au joueur
     * @param player L'instance du joueur à qui vous voulez donner le succès
     * @param advancement L'identifiant du succès à donner
     */
    public void giveAdvancement(Player player, Advancements advancement) {
        giveAdvancement(player, advancement, null);
    }

    /**
     * Retire le succès au joueur
     * @param player L'instance du joueur à qui vous voulez retirer le succès
     * @param advancement L'identifiant du succès à retirer
     */
    public void removeAdvancement(Player player, Advancements advancement) {
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

        Advancements(String category) {
            this.category = category;
        }
    }

}
