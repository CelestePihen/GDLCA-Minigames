package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import fr.cel.gameapi.manager.cosmetic.CosmeticType;
import fr.cel.gameapi.manager.cosmetic.PlayerCosmetics;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Database handler for cosmetics system
 */
public class CosmeticsDatabase {

    private static final String SELECT_ALL_COSMETICS_SQL = "SELECT * FROM cosmetics ORDER BY type, rarity DESC";
    private static final String SELECT_PLAYER_COSMETICS_SQL = "SELECT cosmetic_id FROM player_cosmetics WHERE player_uuid = ?";
    private static final String SELECT_PLAYER_EQUIPPED_COSMETICS_SQL = "SELECT type, cosmetic_id FROM player_equipped_cosmetics WHERE player_uuid = ?";
    private static final String DELETE_EQUIPPED_COSMETIC_SQL = "DELETE FROM player_equipped_cosmetics WHERE player_uuid = ?";
    private static final String INSERT_PLAYER_COSMETIC_SQL = "INSERT INTO player_equipped_cosmetics (player_uuid, type, cosmetic_id) VALUES (?, ?, ?)";
    private static final String INSERT_PLAYER_COSMETIC_OWNED_SQL = "INSERT INTO player_cosmetics (player_uuid, cosmetic_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
    private static final String DELETE_PLAYER_COSMETIC_OWNED_SQL = "DELETE FROM player_cosmetics WHERE player_uuid = ? AND cosmetic_id = ?";
    private static final String DELETE_EQUIPPED_COSMETIC_BY_TYPE_SQL = "DELETE FROM player_equipped_cosmetics WHERE player_uuid = ? AND type = ?";
    private static final String INSERT_PLAYER_EQUIPPED_COSMETIC_SQL = "INSERT INTO player_equipped_cosmetics (player_uuid, type, cosmetic_id) VALUES (?, ?, ?) ON CONFLICT (player_uuid, type) DO UPDATE SET cosmetic_id = EXCLUDED.cosmetic_id";

    private final GameAPI main;

    public CosmeticsDatabase(GameAPI main) {
        this.main = main;
    }

    /**
     * Load all cosmetics from the database
     * @return CompletableFuture with list of cosmetics
     */
    public CompletableFuture<List<Cosmetic>> loadAllCosmetics() {
        return CompletableFuture.supplyAsync(() -> {
            List<Cosmetic> cosmetics = new ArrayList<>();

            try (Connection connection = main.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(SELECT_ALL_COSMETICS_SQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    CosmeticType type = CosmeticType.fromId(rs.getString("type"));
                    Material material = Material.valueOf(rs.getString("display_material"));
                    int rarity = rs.getInt("rarity");
                    int price = rs.getInt("price");
                    String data = rs.getString("data");

                    cosmetics.add(new Cosmetic(id, name, description, type, material, rarity, price, data));
                }

            } catch (SQLException e) {
                main.getComponentLogger().error(Component.text("Error loading cosmetics: " + e.getMessage()));
            }

            return cosmetics;
        });
    }

    /**
     * Load player cosmetics data from the database
     * @param playerUuid The player UUID
     * @return CompletableFuture with PlayerCosmetics
     */
    public CompletableFuture<PlayerCosmetics> loadPlayerCosmetics(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerCosmetics data = new PlayerCosmetics(playerUuid);

            // Load owned cosmetics
            try (Connection connection = main.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(SELECT_PLAYER_COSMETICS_SQL)) {

                ps.setString(1, playerUuid.toString());

                ResultSet rs = ps.executeQuery();
                while (rs.next()) data.addCosmetic(rs.getString("cosmetic_id"));

            } catch (SQLException e) {
                main.getComponentLogger().error(Component.text("Error loading player cosmetics: " + e.getMessage()));
            }

            // Load equipped cosmetics
            try (Connection connection = main.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(SELECT_PLAYER_EQUIPPED_COSMETICS_SQL)) {

                ps.setString(1, playerUuid.toString());

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    CosmeticType type = CosmeticType.fromId(rs.getString("type"));
                    String cosmeticId = rs.getString("cosmetic_id");
                    if (type != null && cosmeticId != null) data.equipCosmetic(type, cosmeticId);
                }

            } catch (SQLException e) {
                main.getComponentLogger().error(Component.text("Error loading equipped cosmetics: " + e.getMessage()));
            }

            return data;
        });
    }

    /**
     * Save player cosmetics data to the database
     * @param data The player cosmetics data
     */
    public void savePlayerCosmetics(PlayerCosmetics data) {
        try (Connection connection = main.getDatabase().getConnection()) {
            // Delete existing equipped cosmetics
            try (PreparedStatement ps = connection.prepareStatement(DELETE_EQUIPPED_COSMETIC_SQL)) {
                ps.setString(1, data.getPlayerUuid().toString());
                ps.executeUpdate();
            }

            // Insert current equipped cosmetics
            try (PreparedStatement ps = connection.prepareStatement(INSERT_PLAYER_COSMETIC_SQL)) {
                for (CosmeticType type : CosmeticType.values()) {
                    String cosmeticId = data.getEquippedCosmetic(type);
                    if (cosmeticId != null) {
                        ps.setString(1, data.getPlayerUuid().toString());
                        ps.setString(2, type.getId());
                        ps.setString(3, cosmeticId);
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }

        } catch (SQLException e) {
            main.getComponentLogger().error(Component.text("Error saving player cosmetics: " + e.getMessage()));
        }
    }

    /**
     * Add a cosmetic to a player's owned collection
     * @param playerUuid The player UUID
     * @param cosmeticId The cosmetic ID
     */
    public void addPlayerCosmetic(UUID playerUuid, String cosmeticId) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = main.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(INSERT_PLAYER_COSMETIC_OWNED_SQL)) {

                ps.setString(1, playerUuid.toString());
                ps.setString(2, cosmeticId);
                ps.executeUpdate();
            } catch (SQLException e) {
                main.getComponentLogger().error(Component.text("Error adding player cosmetic: " + e.getMessage()));
            }
        });
    }

    /**
     * Remove a cosmetic from a player's owned collection
     * @param playerUuid The player UUID
     * @param cosmeticId The cosmetic ID
     */
    public void removePlayerCosmetic(UUID playerUuid, String cosmeticId) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = main.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(DELETE_PLAYER_COSMETIC_OWNED_SQL)) {

                ps.setString(1, playerUuid.toString());
                ps.setString(2, cosmeticId);
                ps.executeUpdate();
            } catch (SQLException e) {
                main.getComponentLogger().error(Component.text("Error removing player cosmetic: " + e.getMessage()));
            }
        });
    }

    /**
     * Update a player's equipped cosmetic
     * @param playerUuid The player UUID
     * @param type The cosmetic type
     * @param cosmeticId The cosmetic ID (null to unequip)
     */
    public void updateEquippedCosmetic(UUID playerUuid, CosmeticType type, String cosmeticId) {
        CompletableFuture.runAsync(() -> {
            if (cosmeticId == null) {
                // Unequip
                try (Connection connection = main.getDatabase().getConnection();
                     PreparedStatement ps = connection.prepareStatement(DELETE_EQUIPPED_COSMETIC_BY_TYPE_SQL)) {

                    ps.setString(1, playerUuid.toString());
                    ps.setString(2, type.getId());
                    ps.executeUpdate();

                } catch (SQLException e) {
                    main.getComponentLogger().error(Component.text("Error unequipping cosmetic: " + e.getMessage()));
                }
            } else {
                // Equip
                try (Connection connection = main.getDatabase().getConnection();
                     PreparedStatement ps = connection.prepareStatement(INSERT_PLAYER_EQUIPPED_COSMETIC_SQL)) {

                    ps.setString(1, playerUuid.toString());
                    ps.setString(2, type.getId());
                    ps.setString(3, cosmeticId);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    main.getComponentLogger().error(Component.text("Error equipping cosmetic: " + e.getMessage()));
                }
            }
        });
    }
}

