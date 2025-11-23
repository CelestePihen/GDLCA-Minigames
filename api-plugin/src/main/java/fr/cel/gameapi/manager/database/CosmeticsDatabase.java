package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import fr.cel.gameapi.manager.cosmetic.CosmeticType;
import fr.cel.gameapi.manager.cosmetic.PlayerCosmetics;
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

    private final GameAPI plugin;

    public CosmeticsDatabase(GameAPI plugin) {
        this.plugin = plugin;
    }

    /**
     * Load all cosmetics from the database
     * @return CompletableFuture with list of cosmetics
     */
    public CompletableFuture<List<Cosmetic>> loadAllCosmetics() {
        return CompletableFuture.supplyAsync(() -> {
            List<Cosmetic> cosmetics = new ArrayList<>();

            String sql = "SELECT * FROM cosmetics ORDER BY type, rarity DESC";

            try (Connection connection = plugin.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql);
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
                plugin.getLogger().severe("Error loading cosmetics: " + e.getMessage());
            }

            return cosmetics;
        });
    }

    /**
     * Save a cosmetic to the database
     * @param cosmetic The cosmetic to save
     */
    public void saveCosmetic(Cosmetic cosmetic) {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON CONFLICT (id) DO UPDATE SET " +
                        "name = EXCLUDED.name, description = EXCLUDED.description, type = EXCLUDED.type, " +
                        "display_material = EXCLUDED.display_material, rarity = EXCLUDED.rarity, " +
                        "price = EXCLUDED.price, data = EXCLUDED.data";

            try (Connection connection = plugin.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, cosmetic.getId());
                ps.setString(2, cosmetic.getName());
                ps.setString(3, cosmetic.getDescription());
                ps.setString(4, cosmetic.getType().getId());
                ps.setString(5, cosmetic.getDisplayMaterial().name());
                ps.setInt(6, cosmetic.getRarity());
                ps.setInt(7, cosmetic.getPrice());
                ps.setString(8, cosmetic.getData());

                ps.executeUpdate();

            } catch (SQLException e) {
                plugin.getLogger().severe("Error saving cosmetic: " + e.getMessage());
            }
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
            String ownedSql = "SELECT cosmetic_id FROM player_cosmetics WHERE uuid_player = ?";
            try (Connection connection = plugin.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(ownedSql)) {

                ps.setString(1, playerUuid.toString());
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    data.addCosmetic(rs.getString("cosmetic_id"));
                }

            } catch (SQLException e) {
                plugin.getLogger().severe("Error loading player cosmetics: " + e.getMessage());
            }

            // Load equipped cosmetics
            String equippedSql = "SELECT type, cosmetic_id FROM player_equipped_cosmetics WHERE uuid_player = ?";
            try (Connection connection = plugin.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(equippedSql)) {

                ps.setString(1, playerUuid.toString());
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    CosmeticType type = CosmeticType.fromId(rs.getString("type"));
                    String cosmeticId = rs.getString("cosmetic_id");
                    if (type != null && cosmeticId != null) {
                        data.equipCosmetic(type, cosmeticId);
                    }
                }

            } catch (SQLException e) {
                plugin.getLogger().severe("Error loading equipped cosmetics: " + e.getMessage());
            }

            return data;
        });
    }

    /**
     * Save player cosmetics data to the database
     * @param data The player cosmetics data
     */
    public void savePlayerCosmetics(PlayerCosmetics data) {
        savePlayerCosmetics(data, true);
    }

    /**
     * Save player cosmetics data to the database
     * @param data The player cosmetics data
     * @param async Whether to save asynchronously
     */
    public void savePlayerCosmetics(PlayerCosmetics data, boolean async) {
        Runnable saveTask = () -> {
            // Note: We don't need to save owned cosmetics here as they're added/removed individually
            // We only need to save equipped cosmetics

            String deleteSql = "DELETE FROM player_equipped_cosmetics WHERE uuid_player = ?";
            String insertSql = "INSERT INTO player_equipped_cosmetics (uuid_player, type, cosmetic_id) VALUES (?, ?, ?)";

            try (Connection connection = plugin.getDatabase().getConnection()) {
                // Delete existing equipped cosmetics
                try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                    ps.setString(1, data.getPlayerUuid().toString());
                    ps.executeUpdate();
                }

                // Insert current equipped cosmetics
                try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
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
                plugin.getLogger().severe("Error saving player cosmetics: " + e.getMessage());
            }
        };

        if (async) {
            CompletableFuture.runAsync(saveTask);
        } else {
            saveTask.run();
        }
    }

    /**
     * Add a cosmetic to a player's owned collection
     * @param playerUuid The player UUID
     * @param cosmeticId The cosmetic ID
     */
    public void addPlayerCosmetic(UUID playerUuid, String cosmeticId) {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO player_cosmetics (uuid_player, cosmetic_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

            try (Connection connection = plugin.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, playerUuid.toString());
                ps.setString(2, cosmeticId);
                ps.executeUpdate();

            } catch (SQLException e) {
                plugin.getLogger().severe("Error adding player cosmetic: " + e.getMessage());
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
            String sql = "DELETE FROM player_cosmetics WHERE uuid_player = ? AND cosmetic_id = ?";

            try (Connection connection = plugin.getDatabase().getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, playerUuid.toString());
                ps.setString(2, cosmeticId);
                ps.executeUpdate();

            } catch (SQLException e) {
                plugin.getLogger().severe("Error removing player cosmetic: " + e.getMessage());
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
                String sql = "DELETE FROM player_equipped_cosmetics WHERE uuid_player = ? AND type = ?";
                try (Connection connection = plugin.getDatabase().getConnection();
                     PreparedStatement ps = connection.prepareStatement(sql)) {

                    ps.setString(1, playerUuid.toString());
                    ps.setString(2, type.getId());
                    ps.executeUpdate();

                } catch (SQLException e) {
                    plugin.getLogger().severe("Error unequipping cosmetic: " + e.getMessage());
                }
            } else {
                // Equip
                String sql = "INSERT INTO player_equipped_cosmetics (uuid_player, type, cosmetic_id) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT (uuid_player, type) DO UPDATE SET cosmetic_id = EXCLUDED.cosmetic_id";
                try (Connection connection = plugin.getDatabase().getConnection();
                     PreparedStatement ps = connection.prepareStatement(sql)) {

                    ps.setString(1, playerUuid.toString());
                    ps.setString(2, type.getId());
                    ps.setString(3, cosmeticId);
                    ps.executeUpdate();

                } catch (SQLException e) {
                    plugin.getLogger().severe("Error equipping cosmetic: " + e.getMessage());
                }
            }
        });
    }
}

