package fr.cel.gameapi.manager.database.event;

import fr.cel.gameapi.GameAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WinterPlayerData {

    private static final String UPDATE_ADD_POINTS_PLAYER_SQL = "UPDATE event_winter2025 SET points = points + ? WHERE uuid_player = ?;";
    private static final String UPDATE_REMOVE_POINTS_PLAYER_SQL = "UPDATE event_winter2025 SET points = points - ? WHERE uuid_player = ?;";
    private static final String SELECT_POINTS_PLAYER_SQL = "SELECT points FROM event_winter2025 WHERE uuid_player = ?;";
    private static final String UPDATE_GIFTS_FOUND_PLAYER_SQL = "UPDATE event_winter2025 SET gifts_found = gifts_found + ? WHERE uuid_player = ?;";
    private static final String SELECT_GIFTS_FOUND_PLAYER_SQL = "SELECT gifts_found FROM event_winter2025 WHERE uuid_player = ?;";

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
            GameAPI.getInstance().getLogger().severe("An error occurred while adding winter points to player " + uuid + ": " + e.getMessage());
        }
    }

    /**
     * Removes winter points from the player.
     * @param amount The amount of winter points to remove
     */
    public void removeWinterPoints(int amount) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_REMOVE_POINTS_PLAYER_SQL)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("An error occurred while removing winter points from player " + uuid + ": " + e.getMessage());
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
            GameAPI.getInstance().getLogger().severe("An error occurred while retrieving winter points for player " + uuid + ": " + e.getMessage());
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
            GameAPI.getInstance().getLogger().severe("An error occurred while adding gifts found to player " + uuid + ": " + e.getMessage());
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
            GameAPI.getInstance().getLogger().severe("An error occurred while retrieving gifts found for player " + uuid + ": " + e.getMessage());
        }
        return giftsFound;
    }

}