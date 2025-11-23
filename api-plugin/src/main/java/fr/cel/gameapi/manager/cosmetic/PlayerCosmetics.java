package fr.cel.gameapi.manager.cosmetic;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents the cosmetics owned and equipped by a player
 */
@Getter
public class PlayerCosmetics {
    private final UUID playerUuid;
    private final Set<String> ownedCosmetics;
    private final Map<CosmeticType, String> equippedCosmetics;

    public PlayerCosmetics(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.ownedCosmetics = new HashSet<>();
        this.equippedCosmetics = new EnumMap<>(CosmeticType.class);
    }

    /**
     * Check if the player owns a specific cosmetic
     * @param cosmeticId The cosmetic ID
     * @return true if owned
     */
    public boolean ownsCosmetic(String cosmeticId) {
        return ownedCosmetics.contains(cosmeticId);
    }

    /**
     * Add a cosmetic to the player's owned collection
     * @param cosmeticId The cosmetic ID
     */
    public void addCosmetic(String cosmeticId) {
        ownedCosmetics.add(cosmeticId);
    }

    /**
     * Remove a cosmetic from the player's owned collection
     * @param cosmeticId The cosmetic ID
     */
    public void removeCosmetic(String cosmeticId) {
        ownedCosmetics.remove(cosmeticId);
    }

    /**
     * Equip a cosmetic of a specific type
     * @param type The cosmetic type
     * @param cosmeticId The cosmetic ID (null to unequip)
     */
    public void equipCosmetic(CosmeticType type, @Nullable String cosmeticId) {
        if (cosmeticId == null) {
            equippedCosmetics.remove(type);
        } else {
            equippedCosmetics.put(type, cosmeticId);
        }
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
     * Unequip all cosmetics
     */
    public void unequipAll() {
        equippedCosmetics.clear();
    }

    /**
     * Unequip a specific cosmetic type
     * @param type The cosmetic type
     */
    public void unequip(CosmeticType type) {
        equippedCosmetics.remove(type);
    }
}

