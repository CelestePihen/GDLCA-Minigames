package fr.cel.gameapi.manager.cosmetic;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.cosmetic.applicator.CosmeticApplicator;
import fr.cel.gameapi.manager.cosmetic.applicator.HatApplicator;
import fr.cel.gameapi.manager.cosmetic.applicator.ParticleApplicator;
import fr.cel.gameapi.manager.cosmetic.applicator.PetApplicator;
import fr.cel.gameapi.manager.database.CosmeticsDatabase;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Main manager for the cosmetics system
 * Handles registration, application, and storage of cosmetics
 */
@Getter
public class CosmeticsManager {

    private final GameAPI main;
    private final CosmeticsDatabase database;
    private final DressingManager dressingManager;

    // Registry of all available cosmetics
    private final Map<String, Cosmetic> cosmeticsRegistry = new HashMap<>();

    // Player cosmetics data (in memory)
    private final Map<UUID, PlayerCosmetics> playerCosmetics = new HashMap<>();

    // Applicators for each cosmetic type
    private final Map<CosmeticType, CosmeticApplicator> applicators = new EnumMap<>(CosmeticType.class);

    public CosmeticsManager(GameAPI main) {
        this.main = main;
        this.database = new CosmeticsDatabase(main);

        // Initialize applicators
        applicators.put(CosmeticType.HAT, new HatApplicator());
        applicators.put(CosmeticType.PARTICLE, new ParticleApplicator());
        applicators.put(CosmeticType.PET, new PetApplicator());

        // Load cosmetics from database
        loadCosmetics();

        this.dressingManager = new DressingManager();
    }

    public Map<CosmeticType, CosmeticApplicator> getApplicators() {
        return new HashMap<>(applicators);
    }

    /**
     * Load all cosmetics from the database
     */
    private void loadCosmetics() {
        database.loadAllCosmetics().thenAccept(cosmetics -> {
            cosmetics.forEach(cosmetic -> cosmeticsRegistry.put(cosmetic.getId(), cosmetic));
            main.getLogger().info("Loaded " + cosmetics.size() + " cosmetics from database");
        });
    }

    /**
     * Get a cosmetic by ID
     * @param id The cosmetic ID
     * @return The cosmetic, or null if not found
     */
    @Nullable
    public Cosmetic getCosmetic(String id) {
        return cosmeticsRegistry.get(id);
    }

    /**
     * Get all cosmetics of a specific type
     * @param type The cosmetic type
     * @return List of cosmetics
     */
    public List<Cosmetic> getCosmeticsByType(CosmeticType type) {
        List<Cosmetic> result = new ArrayList<>();
        for (Cosmetic cosmetic : cosmeticsRegistry.values()) {
            if (cosmetic.getType() == type) {
                result.add(cosmetic);
            }
        }
        return result;
    }

    /**
     * Get all registered cosmetics
     * @return Collection of all cosmetics
     */
    public Collection<Cosmetic> getAllCosmetics() {
        return cosmeticsRegistry.values();
    }

    /**
     * Load player cosmetics data from database
     * @param player The player
     * @return CompletableFuture with the PlayerCosmetics
     */
    public CompletableFuture<PlayerCosmetics> loadPlayerCosmetics(Player player) {
        return database.loadPlayerCosmetics(player.getUniqueId()).thenApply(data -> {
            playerCosmetics.put(player.getUniqueId(), data);
            return data;
        });
    }

    /**
     * Get player cosmetics data (from memory)
     * @param player The player
     * @return The PlayerCosmetics, or null if not loaded
     */
    @Nullable
    public PlayerCosmetics getPlayerCosmetics(Player player) {
        return playerCosmetics.get(player.getUniqueId());
    }

    /**
     * Unload player cosmetics data and save to database
     * @param player The player
     */
    public void unloadPlayerCosmetics(Player player) {
        PlayerCosmetics data = playerCosmetics.remove(player.getUniqueId());
        if (data != null) {
            for (CosmeticType type : CosmeticType.values()) {
                String cosmeticId = data.getEquippedCosmetic(type);
                if (cosmeticId != null) {
                    Cosmetic cosmetic = getCosmetic(cosmeticId);
                    if (cosmetic != null) unapplyCosmetic(player, cosmetic);
                }
            }

            database.savePlayerCosmetics(data);
        }
    }

    /**
     * Give a cosmetic to a player
     * @param player The player
     * @param cosmeticId The cosmetic ID
     * @return true if successful
     */
    public boolean giveCosmetic(Player player, String cosmeticId) {
        PlayerCosmetics data = getPlayerCosmetics(player);
        if (data == null) return false;

        Cosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic == null) return false;

        if (data.ownsCosmetic(cosmeticId)) return false;

        data.addCosmetic(cosmeticId);
        database.addPlayerCosmetic(player.getUniqueId(), cosmeticId);

        return true;
    }

    /**
     * Unlock a cosmetic for a player (alias for giveCosmetic)
     * @param player The player
     * @param cosmeticId The cosmetic ID
     * @return true if successful
     */
    public boolean unlockCosmetic(Player player, String cosmeticId) {
        return giveCosmetic(player, cosmeticId);
    }

    /**
     * Remove a cosmetic from a player
     * @param player The player
     * @param cosmeticId The cosmetic ID
     * @return true if successful
     */
    public boolean removeCosmetic(Player player, String cosmeticId) {
        PlayerCosmetics data = getPlayerCosmetics(player);
        if (data == null) return false;

        if (!data.ownsCosmetic(cosmeticId)) return false;

        // Unequip if equipped
        Cosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic != null && data.isEquipped(cosmeticId)) {
            unequipCosmetic(player, cosmetic.getType());
        }

        data.removeCosmetic(cosmeticId);
        database.removePlayerCosmetic(player.getUniqueId(), cosmeticId);

        return true;
    }

    /**
     * Equip a cosmetic for a player
     * @param player The player
     * @param cosmeticId The cosmetic ID
     * @return true if successful
     */
    public boolean equipCosmetic(Player player, String cosmeticId) {
        PlayerCosmetics data = getPlayerCosmetics(player);
        if (data == null) return false;

        Cosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic == null) return false;

        if (!data.ownsCosmetic(cosmeticId)) return false;

        // Unequip current cosmetic of this type
        String currentEquipped = data.getEquippedCosmetic(cosmetic.getType());
        if (currentEquipped != null) {
            Cosmetic currentCosmetic = getCosmetic(currentEquipped);
            if (currentCosmetic != null) unapplyCosmetic(player, currentCosmetic);
        }

        // Equip the new cosmetic
        data.equipCosmetic(cosmetic.getType(), cosmeticId);
        applyCosmetic(player, cosmetic);

        database.updateEquippedCosmetic(player.getUniqueId(), cosmetic.getType(), cosmeticId);

        return true;
    }

    /**
     * Unequip a cosmetic type for a player
     * @param player The player
     * @param type The cosmetic type
     * @return true if successful
     */
    public boolean unequipCosmetic(Player player, CosmeticType type) {
        PlayerCosmetics data = getPlayerCosmetics(player);
        if (data == null) return false;

        String cosmeticId = data.getEquippedCosmetic(type);
        if (cosmeticId == null) return false;

        Cosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic != null) {
            unapplyCosmetic(player, cosmetic);
        }

        data.unequip(type);
        database.updateEquippedCosmetic(player.getUniqueId(), type, null);

        return true;
    }

    /**
     * Apply a cosmetic to a player
     * @param player The player
     * @param cosmetic The cosmetic
     */
    private void applyCosmetic(Player player, Cosmetic cosmetic) {
        CosmeticApplicator applicator = applicators.get(cosmetic.getType());
        if (applicator != null) applicator.apply(player, cosmetic);
    }

    /**
     * Unapply a cosmetic from a player
     * @param player The player
     * @param cosmetic The cosmetic
     */
    private void unapplyCosmetic(Player player, Cosmetic cosmetic) {
        CosmeticApplicator applicator = applicators.get(cosmetic.getType());
        if (applicator != null) applicator.remove(player, cosmetic);
    }

    /**
     * Reapply all equipped cosmetics for a player
     * @param player The player
     */
    public void reapplyCosmetics(Player player) {
        PlayerCosmetics data = getPlayerCosmetics(player);
        if (data == null) return;

        for (CosmeticType type : CosmeticType.values()) {
            String cosmeticId = data.getEquippedCosmetic(type);
            if (cosmeticId != null) {
                Cosmetic cosmetic = getCosmetic(cosmeticId);
                if (cosmetic != null) applyCosmetic(player, cosmetic);
            }
        }
    }

    /**
     * Shutdown the cosmetics manager
     */
    public void shutdown() {
        // Stop all particle effects
        ParticleApplicator particleApplicator = (ParticleApplicator) applicators.get(CosmeticType.PARTICLE);
        if (particleApplicator != null) particleApplicator.removeAll();

        // Remove all pets
        PetApplicator petApplicator = (PetApplicator) applicators.get(CosmeticType.PET);
        if (petApplicator != null) petApplicator.removeAll();

        playerCosmetics.values().forEach(data -> database.savePlayerCosmetics(data));

        main.getLogger().info("Saved cosmetics data for " + playerCosmetics.size() + " player(s)");
    }
}
