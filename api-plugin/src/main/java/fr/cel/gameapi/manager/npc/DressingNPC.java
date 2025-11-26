package fr.cel.gameapi.manager.npc;

import fr.cel.gameapi.manager.cosmetic.CosmeticType;
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

}