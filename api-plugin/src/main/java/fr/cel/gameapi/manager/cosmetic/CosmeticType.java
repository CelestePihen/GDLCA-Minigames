package fr.cel.gameapi.manager.cosmetic;

import lombok.Getter;

/**
 * Enum representing the different types of cosmetics available in the game
 */
@Getter
public enum CosmeticType {
    HAT("Chapeau", "hat"),
    PARTICLE("Particule", "particle"),
    PET("Compagnon", "pet");

    private final String displayName;
    private final String id;

    CosmeticType(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    /**
     * Get a CosmeticType from its ID
     * @param id The ID to search for
     * @return The CosmeticType, or null if not found
     */
    public static CosmeticType fromId(String id) {
        for (CosmeticType type : values()) {
            if (type.id.equalsIgnoreCase(id)) return type;
        }
        return null;
    }
}


