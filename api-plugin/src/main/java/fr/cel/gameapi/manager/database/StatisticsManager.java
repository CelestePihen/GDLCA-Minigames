package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatisticsManager {

    private final GameAPI main;

    /**
     * Constructeur pour initialiser le gestionnaire de statistiques
     * @param main Instance principale du plugin GameAPI
     */
    public StatisticsManager(GameAPI main) {
        this.main = main;
    }

    /**
     * Permet d'ajouter un montant à une statistique d'un joueur
     * @param player Le joueur à qui modifier la statistique
     * @param statistic La statistique à modifier
     * @param amount Le montant à ajouter à la statistique
     */
    public void updatePlayerStatistic(Player player, PlayerStatistics statistic, int amount) {
        String ps = "UPDATE " + statistic.getTableName() + " SET " + statistic.getColumnName() + " = " + statistic.getColumnName() + " + ? WHERE uuid_player = ?";
        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, player.getUniqueId().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Error updating player statistic: " + e.getMessage());
        }
    }

    /**
     * Permet d'ajouter un montant à une statistique d'un joueur
     * @param uuidPlayer L'UUID du joueur à qui modifier la statistique
     * @param statistic La statistique à modifier
     * @param amount Le montant à ajouter à la statistique
     */
    public void updatePlayerStatistic(UUID uuidPlayer, PlayerStatistics statistic, int amount) {
        String ps = "UPDATE " + statistic.getTableName() + " SET " + statistic.getColumnName() + " = " + statistic.getColumnName() + " + ? WHERE uuid_player = ?";
        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuidPlayer.toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Error updating player statistic: " + e.getMessage());
        }
    }

    /**
     * Permet d'obtenir le montant de la statistique d'un joueur
     * @param player Le joueur à qui obtenir la statistique
     * @param tableName Le nom de la table de la statistique
     */
    public int getPlayerStatistic(Player player, PlayerStatistics tableName) {
        String ps = "SELECT " + tableName.getColumnName() + " FROM " + tableName.getTableName() + " WHERE uuid_player = ?";
        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, player.getUniqueId().toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(tableName.getColumnName());
                }
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Error retrieving player statistic: " + e.getMessage());
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
         * Constructeur pour initialiser le nom de la table et le nom de la colonne
         * @param tableName Nom de la table dans la base de données
         * @param columnName Nom de la colonne dans la table
         */
        PlayerStatistics(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
        }
    }

    /**
     * Enregistre une partie de Cache-Cache dans la base de données
     * @param mapName Nom de la carte
     * @param hiders UUIDs des joueurs
     * @param seekers UUIDs des chercheurs de départ
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
            GameAPI.getInstance().getLogger().severe("Error: Adding " + mapName + " to CC game statistic: " + e.getMessage());
        }

        return generatedId;
    }

    /**
     * Met à jour le temps d'une partie terminée de Cache-Cache
     * @param gameId Identifiant de la partie
     */
    public void updateCCGameEnd(int gameId) {
        String sql = "UPDATE cc_maps_statistics SET timestamp_end = NOW() WHERE id = ?;";

        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gameId);
            statement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Error: Updating " + gameId + " to CC game statistic: " + e.getMessage());
        }
    }

    /**
     * Enregistre une partie de Valocraft dans la base de données
     * @param mapName Nom de la carte
     * @param attackersUUID UUIDs des attaquants de départ
     * @param defendersUUID UUIDs des défenseurs de départ
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
            GameAPI.getInstance().getLogger().severe("Error adding Valocraft game statistic: " + e.getMessage());
        }

        return generatedId;
    }

    /**
     * Met à jour une partie terminée de Valocraft
     * @param gameId Identifiant de la partie
     * @param attackersScore Le score des attaquants
     * @param defendersScore Le score des défenseurs
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
            GameAPI.getInstance().getLogger().severe("Error: Updating " + gameId + " to CC game statistic: " + e.getMessage());
        }
    }

}