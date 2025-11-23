package fr.cel.gameapi.manager.database.event;

import fr.cel.gameapi.GameAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WinterPlayerData {

    private static final String UPDATE_ADD_POINTS_PLAYER_SQL = "UPDATE event_winter2025 SET points = points + ? WHERE uuid_player = ?;";
    private static final String UPDATE_REMOVE_POINTS_PLAYER_SQL = "UPDATE event_winter2025 SET points = points - ? WHERE uuid_player = ?;";
    private static final String SELECT_POINTS_PLAYER_SQL = "SELECT points FROM event_winter2025 WHERE uuid_player = ?;";
    private static final String UPDATE_GIFTS_FOUND_PLAYER_SQL = "UPDATE event_winter2025 SET gifts_found = gifts_found + ? WHERE uuid_player = ?;";
    private static final String SELECT_GIFTS_FOUND_PLAYER_SQL = "SELECT gifts_found FROM event_winter2025 WHERE uuid_player = ?;";
    private static final String SELECT_POINTS_LEADERBOARD_SQL = "SELECT uuid_player FROM event_winter2025 ORDER BY points DESC;";

    private final UUID uuid;

    public WinterPlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Adds winter points to the player.
     * @param amount The amount of winter points to add
     */
    public void addWinterPoints(int amount) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ADD_POINTS_PLAYER_SQL)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while adding winter points to player " + uuid + ": " + e.getMessage(), NamedTextColor.RED));
        }
    }

    /**
     * Removes winter points from the player.
     * @param amount The amount of winter points to remove
     */
    public boolean removeWinterPoints(int amount) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_REMOVE_POINTS_PLAYER_SQL)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while removing winter points from player " + uuid + ": " + e.getMessage(), NamedTextColor.RED));
            return false;
        }
    }

    /**
     * Retrieves the amount of winter points the player has.
     * @return The number of winter points
     */
    public int getWinterPoints() {
        int points = 0;
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POINTS_PLAYER_SQL)) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                points = resultSet.getInt("points");
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while retrieving winter points for player " + uuid + ": " + e.getMessage(), NamedTextColor.RED));
        }
        return points;
    }

    /**
     * Adds gifts found to the player.
     * @param giftsFound The amount of gifts found by the player to add
     */
    public void addGiftsFound(int giftsFound) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GIFTS_FOUND_PLAYER_SQL)) {
            preparedStatement.setInt(1, giftsFound);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while adding gifts found to player " + uuid + ": " + e.getMessage(), NamedTextColor.RED));
        }
    }

    /**
     * Retrieves the amount of gifts the player has.
     * @return The number of gifts found by the player
     */
    public int getGifts() {
        int giftsFound = 0;
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GIFTS_FOUND_PLAYER_SQL)) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                giftsFound = resultSet.getInt("gifts_found");
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while retrieving gifts found for player " + uuid + ": " + e.getMessage(), NamedTextColor.RED));
        }
        return giftsFound;
    }

    /**
     * Retrieves all players sorted by their winter points in descending order.
     * @return A list of player UUIDs sorted by winter points
     */
    public static List<UUID> getAllPlayersSortedByPoints() {
        List<UUID> players = new ArrayList<>();
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POINTS_LEADERBOARD_SQL);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                players.add(UUID.fromString(rs.getString("uuid_player")));
            }

        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Erreur récupération classement : " + e.getMessage(), NamedTextColor.RED));
        }

        return players;
    }

}