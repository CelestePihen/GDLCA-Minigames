package fr.cel.gameapi.manager.cosmetic.applicator;

import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;

/**
 * Interface for applying and removing cosmetics to/from players
 */
public interface CosmeticApplicator {

    /**
     * Apply a cosmetic to a player
     * @param player The player
     * @param cosmetic The cosmetic to apply
     */
    void apply(Player player, Cosmetic cosmetic);

    /**
     * Remove a cosmetic from a player
     * @param player The player
     * @param cosmetic The cosmetic to remove
     */
    void remove(Player player, Cosmetic cosmetic);

    /**
     * Refresh/update the cosmetic for a player
     * @param player The player
     * @param cosmetic The cosmetic to refresh
     */
    default void refresh(Player player, Cosmetic cosmetic) {
        remove(player, cosmetic);
        apply(player, cosmetic);
    }

    /**
     * Apply a cosmetic to a mannequin
     * @param mannequin The mannequin
     * @param cosmetic The cosmetic to apply
     */
    void apply(Mannequin mannequin, Cosmetic cosmetic);

    /**
     * Remove a cosmetic from a mannequin
     * @param mannequin The mannequin
     * @param cosmetic The cosmetic to remove
     */
    void remove(Mannequin mannequin, Cosmetic cosmetic);

    /**
     * Refresh/update the cosmetic for a mannequin
     * @param mannequin The mannequin
     * @param cosmetic The cosmetic to refresh
     */
    default void refresh(Mannequin mannequin, Cosmetic cosmetic) {
        remove(mannequin, cosmetic);
        apply(mannequin, cosmetic);
    }
}

