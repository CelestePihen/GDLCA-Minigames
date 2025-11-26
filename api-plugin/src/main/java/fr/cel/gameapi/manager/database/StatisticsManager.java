package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatisticsManager {

    private static final String UPDATE_STATISTIC_QUERY = "UPDATE %s SET %s = %s + ? WHERE player_uuid = ?";
    private static final String GET_STATISTIC_QUERY = "SELECT %s FROM %s WHERE player_uuid = ?";

    private final GameAPI main;

    public StatisticsManager(GameAPI main) {
        this.main = main;
    }

    /**
     * Adds a value to a player's statistic.
     * @param player The player whose statistic to modify
     * @param statistic The statistic to update
     * @param amount The amount to add
     */
    public void updatePlayerStatistic(Player player, PlayerStatistics statistic, int amount) {
        String sql = String.format(UPDATE_STATISTIC_QUERY,
                statistic.getTableName(), statistic.getColumnName(), statistic.getColumnName());

        try (Connection connection = main.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, player.getUniqueId().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text(
                    String.format("Error updating player statistic for %s for the table %s and the column %s: %s",
                            player.getName(), statistic.getTableName(), statistic.getColumnName(), e.getMessage())));
        }
    }

    /**
     * Adds a value to a player's statistic using UUID.
     * @param uuidPlayer The UUID of the player
     * @param statistic The statistic to update
     * @param amount The amount to add
     */
    public void updatePlayerStatistic(UUID uuidPlayer, PlayerStatistics statistic, int amount) {
        String sql = String.format(UPDATE_STATISTIC_QUERY,
                statistic.getTableName(), statistic.getColumnName(), statistic.getColumnName());

        try (Connection connection = main.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuidPlayer.toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text(
                    String.format("Error updating player statistic for UUID %s for the table %s and the column %s: %s",
                            uuidPlayer.toString(), statistic.getTableName(), statistic.getColumnName(), e.getMessage())));
        }
    }

    /**
     * Retrieves a player's statistic.
     * @param player The player
     * @param tableName The statistic to retrieve
     * @return The statistic value
     */
    public int getPlayerStatistic(Player player, PlayerStatistics tableName) {
        String sql = String.format(GET_STATISTIC_QUERY, tableName.getColumnName(), tableName.getTableName());

        try (Connection connection = main.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, player.getUniqueId().toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return resultSet.getInt(tableName.getColumnName());
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Error retrieving player statistic: " + e.getMessage()));
        }

        return 0;
    }

    @Getter
    public enum PlayerStatistics {
        // HUB
        HUB_DJ("hub_statistics", "djParticipations"),

        // CACHE-CACHE
        CC_GAMES_PLAYED("cc_statistics", "gamesplayed"),
        CC_SEEKER_COUNT("cc_statistics", "seekercount"),
        CC_HIDER_COUNT("cc_statistics", "hidercount"),
        CC_ELIMINATIONS("cc_statistics", "eliminations"),
        CC_BLINDNESS_USAGE("cc_statistics", "blindnessusage"),
        CC_INVISIBILITY_USAGE("cc_statistics", "invisibilityusage"),
        CC_POINT_PLAYER_USAGE("cc_statistics", "pointplayerusage"),
        CC_SOUND_USAGE("cc_statistics", "soundusage"),
        CC_SPEED_USAGE("cc_statistics", "speedusage"),

        // VALOCRAFT
        VALO_GAMES_PLAYED("valo_statistics", "gamesplayed"),
        VALO_ELIMINATIONS("valo_statistics", "eliminations"),
        VALO_ATTACKER_WINS("valo_statistics", "attackerwins"),
        VALO_DEFENDER_WINS("valo_statistics", "defenderwins"),
        VALO_SPIKES_PLANTED("valo_statistics", "spikesplanted"),
        VALO_SPIKES_DEFUSED("valo_statistics", "spikesdefused"),

        // PVP
        PVP_SWORD_KILLS("pvp_statistics", "swordkills"),
        PVP_BOW_KILLS("pvp_statistics", "bowkills"),
        PVP_TRIDENT_KILLS("pvp_statistics", "tridentkills"),

        // PARKOUR
        PARK_JUMPS("parkour_statistics", "jumps"),
        PARK_PARKOURS_COMPLETED("parkour_statistics", "parkourscompleted"),
        PARK_CHECKPOINTS_TAKEN("parkour_statistics", "checkpointstaken");

        private final String tableName;
        private final String columnName;

        /**
         * Constructor to initialize table and column names.
         * @param tableName Database table name
         * @param columnName Column name in the table
         */
        PlayerStatistics(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
        }
    }

    /**
     * Records a Cache-Cache game in the database.
     * @param mapName Name of the map
     * @param hiders UUIDs of hiders
     * @param seekers UUIDs of seekers
     * @return The generated game ID
     */
    public int addCCGameStatistic(String mapName, List<UUID> hiders, List<UUID> seekers) {
        int generatedId = -1;

        String hidersUuid = hiders.stream().map(UUID::toString).collect(Collectors.joining(";"));
        String seekersUuid = seekers.stream().map(UUID::toString).collect(Collectors.joining(";"));

        String query = "INSERT INTO cc_maps_statistics (mapname, hidersuuid, seekersuuid, timestamp_start) VALUES (?, ?, ?, NOW()) RETURNING id;";

        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, mapName);
            preparedStatement.setString(2, hidersUuid);
            preparedStatement.setString(3, seekersUuid);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                generatedId = rs.getInt("id");
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Error: Adding " + mapName + " to CC game statistic: " + e.getMessage()));
        }

        return generatedId;
    }

    /**
     * Updates the end time of a completed Cache-Cache game.
     * @param gameId The ID of the game
     */
    public void updateCCGameEnd(int gameId) {
        String sql = "UPDATE cc_maps_statistics SET timestamp_end = NOW() WHERE id = ?;";

        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gameId);
            statement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Error: Updating " + gameId + " to CC game statistic: " + e.getMessage()));
        }
    }

    /**
     * Records a Valocraft game in the database.
     * @param mapName Name of the map
     * @param attackersUUID UUIDs of attackers
     * @param defendersUUID UUIDs of defenders
     * @return The generated game ID
     */
    public int addValoGameStatistic(String mapName, List<UUID> attackersUUID, List<UUID> defendersUUID) {
        int generatedId = -1;

        String attackersUuid = attackersUUID.stream().map(UUID::toString).collect(Collectors.joining(";"));
        String defendersUuid = defendersUUID.stream().map(UUID::toString).collect(Collectors.joining(";"));

        String query = "INSERT INTO valo_game_statistics (mapName, attackersUUID, defendersUUID, timestamp_start) VALUES (?, ?, ?, NOW()) RETURNING id;";

        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, mapName);
            preparedStatement.setString(2, attackersUuid);
            preparedStatement.setString(3, defendersUuid);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                generatedId = rs.getInt("id");
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Error adding Valocraft game statistic: " + e.getMessage()));
        }

        return generatedId;
    }

    /**
     * Updates a completed Valocraft game in the database.
     * @param gameId The ID of the game
     * @param attackersScore Score of the attackers
     * @param defendersScore Score of the defenders
     */
    public void updateValoGameEnd(int gameId, int attackersScore, int defendersScore) {
        String sql = "UPDATE valo_maps_statistics SET timestamp_end = NOW(), attackers_score = ?, defenders_score = ? WHERE id = ?;";

        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, attackersScore);
            statement.setInt(2, defendersScore);
            statement.setInt(3, gameId);
            statement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Error: Updating " + gameId + " to CC game statistic: " + e.getMessage()));
        }
    }

}