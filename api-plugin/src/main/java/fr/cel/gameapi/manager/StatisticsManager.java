package fr.cel.gameapi.manager;

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
        String ps = "UPDATE " + statistic.getTableName() + " SET " + statistic + " = " + statistic + " + ? WHERE uuid_player = ?";
        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, player.getUniqueId().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'obtenir le montant de la statistique d'un joueur
     * @param player Le joueur à qui obtenir la statistique
     * @param tableName Le nom de la table de la statistique
     * @param statistic La statistique
     */
    public int getPlayerStatistic(Player player, PlayerStatistics tableName, String statistic) {
        String ps = "SELECT " + statistic + " FROM " + tableName.getTableName() + " WHERE uuid_player = ?";
        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, player.getUniqueId().toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(statistic);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Getter
    public enum PlayerStatistics {
        // HUB
        HUB_DJ("hub_statistics", "djParticipations"),

        // CACHE-CACHE
        CC_GAMES_PLAYED("cc_statistics", "gamesPlayed"),
        CC_SEEKER_COUNT("cc_statistics", "seekerCount"),
        CC_HIDER_COUNT("cc_statistics", "hiderCount"),
        CC_ELIMINATIONS_CC("cc_statistics", "eliminations"),
        CC_BLINDNESS_USAGE("cc_statistics", "blindnessUsage"),
        CC_INVISIBILITY_USAGE("cc_statistics", "invisibilityUsage"),
        CC_SEE_PLAYER_USAGE("cc_statistics", "seePlayerUsage"),
        CC_SOUND_USAGE("cc_statistics", "soundUsage"),
        CC_SPEED_USAGE("cc_statistics", "speedUsage"),

        // VALOCRAFT
        VALO_GAMES_PLAYED("valo_statistics", "gamesPlayed"),
        VALO_ELIMINATIONS("valo_statistics", "eliminations"),
        VALO_ATTACKER_WINS("valo_statistics", "attackerWins"),
        VALO_DEFENDER_WINS("valo_statistics", "defenderWins"),
        VALO_SPIKES_PLANTED("valo_statistics", "spikesPlanted"),
        VALO_SPIKES_DEFUSED("valo_statistics", "spikesDefused"),

        // PVP
        PVP_SWORD_KILLS("pvp_statistics", "swordKills"),
        PVP_BOW_KILLS("pvp_statistics", "bowKills"),
        PVP_TRIDENT_KILLS("pvp_statistics", "tridentKills"),

        // PARKOUR
        PARK_JUMPS("parkour_statistics", "jumps"),
        PARK_PARKOURS_COMPLETED("parkour_statistics", "parkoursCompleted"),
        PARK_CHECKPOINTS_TAKEN("parkour_statistics", "checkpointsTaken");

        private final String tableName;
        private final String columnName;

        PlayerStatistics(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
        }
    }

    /**
     * Enregistre une partie de Cache-Cache terminée dans la base de données
     * @param mapName Nom de la carte
     * @param time Temps total de la partie en secondes
     * @param playersUUID UUIDs des joueurs
     * @param seekersUUID UUIDs des chercheurs de départ
     */
    public void addCCGameStatistic(String mapName, int time, List<UUID> playersUUID, List<UUID> seekersUUID) {
        String query = "INSERT INTO cc_game_statistics (mapName, time, playersUUID, seekersUUID) VALUES (?, ?, ?, ?)";

        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, mapName);
            preparedStatement.setInt(2, time);
            preparedStatement.setString(3, playersUUID.stream().map(UUID::toString).collect(Collectors.joining(",")));
            preparedStatement.setString(4, seekersUUID.stream().map(UUID::toString).collect(Collectors.joining(",")));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enregistre une partie de Valocraft terminée dans la base de données
     * @param mapName Nom de la carte
     * @param time Temps total de la partie en secondes
     * @param attackersUUID UUIDs des attaquants de départ
     * @param defendersUUID UUIDs des défenseurs de départ
     */
    public void addValoGameStatistic(String mapName, int time, List<UUID> attackersUUID, List<UUID> defendersUUID) {
        String query = "INSERT INTO valo_game_statistics (mapName, time, attackersUUID, defendersUUID) VALUES (?, ?, ?, ?)";

        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, mapName);
            preparedStatement.setInt(2, time);
            preparedStatement.setString(3, attackersUUID.stream().map(UUID::toString).collect(Collectors.joining(",")));
            preparedStatement.setString(4, defendersUUID.stream().map(UUID::toString).collect(Collectors.joining(",")));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Getter
    public enum GameStatistics {
        // CACHE-CACHE
        // Nom de la map, durée de la partie, UUIDs des joueurs, UUIDs des chercheurs de départ
        CC_MAP_NAME("cc_game_statistics", "mapName"),
        CC_TIME("cc_game_statistics", "totalTime"),
        CC_PLAYERS_UUID("cc_game_statistics", "playersUUID"),
        CC_SEEKERS_UUID("cc_game_statistics", "seekersUUID"),

        // VALOCRAFT
        // Nom de la map, durée de la partie, UUIDs des attaquants de départ, UUIDs des défenseurs de départ
        VALO_MAP_NAME("valo_game_statistics", "mapName"),
        VALO_TIME("valo_game_statistics", "totalTime"),
        VALO_ATTACKERS_UUID("valo_game_statistics", "attackersUUID"),
        VALO_DEFENDERS_UUID("valo_game_statistics", "defendersUUID");

        private final String tableName;
        private final String columnName;

        GameStatistics(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
        }
    }

}