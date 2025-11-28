package fr.cel.gameapi.manager.npc;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import fr.cel.gameapi.manager.cosmetic.CosmeticType;
import fr.cel.gameapi.manager.cosmetic.CosmeticsManager;
import fr.cel.gameapi.manager.cosmetic.applicator.CosmeticApplicator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Class representing a dressed NPC with equipped cosmetics
 */
public class DressingNPC extends NPC {
    private final Map<CosmeticType, String> equippedCosmetics;

    private final CosmeticsManager cosmeticsManager = GameAPI.getInstance().getCosmeticsManager();

    public DressingNPC(@NotNull Player player, @NotNull Location location) {
        super(player.getName(), null, location);
        this.equippedCosmetics = new EnumMap<>(CosmeticType.class);
    }

    /**
     * Get the equipped cosmetic of a specific type
     * @param type The cosmetic type
     * @return The cosmetic ID, or null if none equipped
     */
    @Nullable
    public String getEquippedCosmetic(CosmeticType type) {
        return equippedCosmetics.get(type);
    }

    /**
     * Check if a cosmetic is currently equipped
     * @param cosmeticId The cosmetic ID
     * @return true if equipped
     */
    public boolean isEquipped(String cosmeticId) {
        return equippedCosmetics.containsValue(cosmeticId);
    }

    /**
     * Equip a cosmetic of a specific type
     * @param type The cosmetic type
     * @param cosmeticId The cosmetic ID
     */
    public void equipCosmetic(CosmeticType type, @NotNull String cosmeticId) {
        equippedCosmetics.put(type, cosmeticId);
    }

    /**
     * Unequip a specific cosmetic type
     * @param type The cosmetic type
     */
    public void unequip(CosmeticType type) {
        equippedCosmetics.remove(type);
    }

    public boolean equipCosmetic(String cosmeticId) {
        Cosmetic cosmetic = cosmeticsManager.getCosmetic(cosmeticId);
        if (cosmetic == null) return false;

        String currentEquipped = getEquippedCosmetic(cosmetic.getType());
        if (currentEquipped != null) {
            Cosmetic currentCosmetic = cosmeticsManager.getCosmetic(currentEquipped);
            if (currentCosmetic != null) unapplyCosmetic(currentCosmetic);
        }

        equipCosmetic(cosmetic.getType(), cosmeticId);
        applyCosmetic(cosmetic);
        return true;
    }

    public boolean unequipCosmetic(CosmeticType type) {
        String cosmeticId = getEquippedCosmetic(type);
        if (cosmeticId == null) return false;

        Cosmetic cosmetic = cosmeticsManager.getCosmetic(cosmeticId);
        if (cosmetic != null) {
            unapplyCosmetic(cosmetic);
        }

        unequip(type);
        return true;
    }

    public void applyCosmetic(Cosmetic cosmetic) {
        CosmeticApplicator applicator = cosmeticsManager.getApplicators().get(cosmetic.getType());
        if (applicator != null) applicator.apply(getMannequin(), cosmetic);
    }

    public void unapplyCosmetic(Cosmetic cosmetic) {
        CosmeticApplicator applicator = cosmeticsManager.getApplicators().get(cosmetic.getType());
        if (applicator != null) applicator.remove(getMannequin(), cosmetic);
    }

}