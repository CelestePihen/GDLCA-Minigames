package fr.cel.gameapi.manager.database.event;

import fr.cel.gameapi.GameAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WinterPlayerData {

    private static final String UPDATE_ADD_POINTS_PLAYER_SQL = "UPDATE event_winter2025 SET points = points + ? WHERE player_uuid = ?;";
    private static final String UPDATE_REMOVE_POINTS_PLAYER_SQL = "UPDATE event_winter2025 SET points = points - ? WHERE player_uuid = ?;";
    private static final String SELECT_POINTS_PLAYER_SQL = "SELECT points FROM event_winter2025 WHERE player_uuid = ?;";

    private static final String UPDATE_GIFTS_FOUND_PLAYER_SQL = "UPDATE event_winter2025 SET gifts_found = gifts_found + ? WHERE player_uuid = ?;";
    private static final String SELECT_GIFTS_FOUND_PLAYER_SQL = "SELECT gifts_found FROM event_winter2025 WHERE player_uuid = ?;";
    private static final String SELECT_POINTS_LEADERBOARD_SQL = "SELECT player_uuid FROM event_winter2025 ORDER BY points DESC;";

    private static final String INSERT_HEAD_PLAYER_SQL = "INSERT INTO event_winter2025_heads VALUES (?, ?);";
    private static final String SELECT_HEADS_PLAYER_SQL = "SELECT head_id FROM event_winter2025_heads WHERE player_uuid = ?;";

    @NotNull private final UUID uuid;
    @Nullable private final Player player;

    public WinterPlayerData(@NotNull UUID uuid, @NotNull Player player) {
        this.uuid = uuid;
        this.player = player;
    }

    public WinterPlayerData(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
    }

    /**
     * Adds winter points to the player.
     * @param amount The amount of winter points to add
     */
    public void addWinterPoints(int amount) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ADD_POINTS_PLAYER_SQL)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while adding winter points to player " + getPlayerName() + ": " + e.getMessage(), NamedTextColor.RED));
        }
    }

    /**
     * Removes winter points from the player.
     * @param amount The amount of winter points to remove
     */
    public boolean removeWinterPoints(int amount) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_REMOVE_POINTS_PLAYER_SQL)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while removing winter points from player " + getPlayerName() + ": " + e.getMessage(), NamedTextColor.RED));
            return false;
        }
    }

    /**
     * Retrieves the amount of winter points the player has.
     * @return The number of winter points
     */
    public int getWinterPoints() {
        int points = 0;
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POINTS_PLAYER_SQL)) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                points = resultSet.getInt("points");
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while retrieving winter points for player " + getPlayerName() + ": " + e.getMessage(), NamedTextColor.RED));
        }
        return points;
    }

    /**
     * Adds gifts found to the player.
     * @param giftsFound The amount of gifts found by the player to add
     */
    public void addGiftsFound(int giftsFound) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GIFTS_FOUND_PLAYER_SQL)) {
            preparedStatement.setInt(1, giftsFound);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while adding gifts found to player " + getPlayerName() + ": " + e.getMessage(), NamedTextColor.RED));
        }
    }

    /**
     * Retrieves the amount of gifts the player has.
     * @return The number of gifts found by the player
     */
    public int getGifts() {
        int giftsFound = 0;
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GIFTS_FOUND_PLAYER_SQL)) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                giftsFound = resultSet.getInt("gifts_found");
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while retrieving gifts found for player " + getPlayerName() + ": " + e.getMessage(), NamedTextColor.RED));
        }
        return giftsFound;
    }


    public static @NotNull List<UUID> getAllPlayersSortedByPoints() {
        List<UUID> players = new ArrayList<>();
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POINTS_LEADERBOARD_SQL);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                players.add(UUID.fromString(rs.getString("player_uuid")));
            }

        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while retrieving the leaderboard of winter points" + e.getMessage(), NamedTextColor.RED));
        }

        return players;
    }

    /**
     * Adds head found to the player.
     * @param headId The head's id found by the player to add
     */
    public void addHeadFound(int headId) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_HEAD_PLAYER_SQL)) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, headId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while adding gifts found to player " + getPlayerName() + ": " + e.getMessage(), NamedTextColor.RED));
        }
    }

    /**
     * Retrieves the heads the player has.
     * @return The heads found by the player
     */
    public List<Integer> getHeads() {
        List<Integer> headsId = new ArrayList<>();
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_HEADS_PLAYER_SQL)) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) headsId.add(resultSet.getInt("head_id"));
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while retrieving gifts found for player " + getPlayerName() + ": " + e.getMessage(), NamedTextColor.RED));
        }
        return headsId;
    }

    private @NotNull String getPlayerName() {
        return player.isOnline() ? player.getName() + "(uuid: " + uuid + ") " : uuid + " ";
    }

}