package fr.cel.gameapi.manager.cosmetic;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

/**
 * Represents a cosmetic item that can be owned and equipped by players
 */
@Getter
public class Cosmetic {
    private final String id;
    private final String name;
    private final String description;
    private final CosmeticType type;
    private final Material displayMaterial;
    private final int rarity; // 1-5 : commun, rare, épique, légendaire, mythique
    private final int price;
    private final String data; // JSON data for specific cosmetic configuration

    public Cosmetic(String id, String name, String description, CosmeticType type,
                    Material displayMaterial, int rarity, int price, String data) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.displayMaterial = displayMaterial;
        this.rarity = rarity;
        this.price = price;
        this.data = data;
    }

    /**
     * Check if the cosmetic is free
     * @return true if the price is 0
     */
    public boolean isFree() {
        return price == 0;
    }

    @Override
    public String toString() {
        return "Cosmetic{id='" + id + "', name='" + name + "', type=" + type + "}";
    }

    public static NamedTextColor getRarityColor(Cosmetic cosmetic) {
        return switch (cosmetic.getRarity()) {
            case 1 -> NamedTextColor.WHITE;
            case 2 -> NamedTextColor.BLUE;
            case 3 -> NamedTextColor.DARK_PURPLE;
            case 4 -> NamedTextColor.GOLD;
            case 5 -> NamedTextColor.RED;
            default -> NamedTextColor.GRAY;
        };
    }

    public static String getRarityName(Cosmetic cosmetic) {
        return switch (cosmetic.getRarity()) {
            case 1 -> "Commun";
            case 2 -> "Rare";
            case 3 -> "Épique";
            case 4 -> "Légendaire";
            case 5 -> "Mythique";
            default -> "Inconnu";
        };
    }
}

