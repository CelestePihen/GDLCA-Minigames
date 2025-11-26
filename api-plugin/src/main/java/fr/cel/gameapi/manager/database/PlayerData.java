package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.event.WinterPlayerData;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerData {

    // TODO: Change uuid_player to player_uuid for consistency
    private static final String UPDATE_ADD_COINS_PLAYER_SQL = "UPDATE players SET coins = coins + ? WHERE uuid_player = ?;";
    private static final String REMOVE_COINS_PLAYER_SQL = "UPDATE players SET coins = coins - ? WHERE uuid_player = ?;";
    private static final String SELECT_COINS_PLAYER_SQL = "SELECT coins FROM players WHERE uuid_player = ?;";
    private static final String SELECT_ALLOW_FRIENDS_PLAYER_SQL = "SELECT allowFriends FROM players WHERE uuid_player = ?;";
    private static final String UPDATE_SET_ALLOW_FRIENDS_PLAYER_SQL = "UPDATE players SET allowFriends = ? WHERE uuid_player = ?;";

    @NotNull private final UUID uuid;
    @NotNull private final Player player;

    @Getter private final WinterPlayerData winterPlayerData;

    public PlayerData(@NotNull Player player) {
        this.uuid = player.getUniqueId();
        this.player = player;
        this.winterPlayerData = new WinterPlayerData(this.uuid, this.player);
    }

    /**
     * Adds coins to the player.
     * @param amount The amount of coins to add
     */
    public void addCoins(double amount) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ADD_COINS_PLAYER_SQL)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while adding coins to player " + getPlayerName() + ": " + e.getMessage()));
        }
    }

    /**
     * Removes coins from the player.
     * @param amount The amount of coins to remove
     */
    public void removeCoins(double amount) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_COINS_PLAYER_SQL)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while removing coins from player " + getPlayerName() + ": " + e.getMessage()));
        }
    }

    /**
     * Retrieves the amount of coins the player has.
     * @return The number of coins
     */
    public double getCoins() {
        double coins = 0.0D;
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COINS_PLAYER_SQL)) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) coins = resultSet.getDouble("coins");
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while retrieving coins for player " + getPlayerName() + ": " + e.getMessage()));
        }

        return coins;
    }

    /**
     * Checks if the player allows friend requests.
     * @return true if the player allows friend requests, false otherwise
     */
    public boolean isAllowingFriends() {
        boolean allow = true;
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALLOW_FRIENDS_PLAYER_SQL)) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) allow = resultSet.getBoolean("allowFriends");
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while checking if player " + getPlayerName() + " allows friend requests: " + e.getMessage()));
        }
        return allow;
    }

    /**
     * Sets whether the player allows friend requests.
     * @param isAllowing true if the player allows friend requests, false otherwise
     */
    public void setAllowFriends(boolean isAllowing) {
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SET_ALLOW_FRIENDS_PLAYER_SQL)) {
            preparedStatement.setBoolean(1, isAllowing);
            preparedStatement.setString(2, uuid.toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("An error occurred while updating friend request settings for player " + getPlayerName() + ": " + e.getMessage()));
        }
    }

    private @NotNull String getPlayerName() {
        return player.isOnline() ? player.getName() + "(uuid: " + uuid + ") " : uuid + " ";
    }

}