package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.event.WinterPlayerData;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    @Getter private final WinterPlayerData winterPlayerData;

    public PlayerData(Player player, WinterPlayerData winterPlayerData) {
        this.uuid = player.getUniqueId();
        this.winterPlayerData = winterPlayerData;
    }

    /**
     * Adds coins to the player.
     * @param amount The amount of coins to add
     */
    public void addCoins(double amount) {
        String ps = "UPDATE players SET coins = coins + ? WHERE uuid_player = ?;";
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("An error occurred while adding coins to player " + uuid + ": " + e.getMessage());
        }
    }

    /**
     * Removes coins from the player.
     * @param amount The amount of coins to remove
     */
    public void removeCoins(double amount) {
        String ps = "UPDATE players SET coins = coins - ? WHERE uuid_player = ?;";
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("An error occurred while removing coins from player " + uuid + ": " + e.getMessage());
        }
    }

    /**
     * Retrieves the amount of coins the player has.
     * @return The number of coins
     */
    public double getCoins() {
        String ps = "SELECT coins FROM players WHERE uuid_player = ?;";
        double coins = 0.0D;
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                coins = resultSet.getDouble("coins");
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("An error occurred while retrieving coins for player " + uuid + ": " + e.getMessage());
        }
        return coins;
    }

    /**
     * Checks if the player allows friend requests.
     * @return true if the player allows friend requests, false otherwise
     */
    public boolean isAllowingFriends() {
        String ps = "SELECT allowFriends FROM players WHERE uuid_player = ?;";
        boolean allow = true;
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                allow = resultSet.getBoolean("allowFriends");
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("An error occurred while checking if player " + uuid + " allows friend requests: " + e.getMessage());
        }
        return allow;
    }

    /**
     * Sets whether the player allows friend requests.
     * @param isAllowing true if the player allows friend requests, false otherwise
     */
    public void setAllowFriends(boolean isAllowing) {
        String ps = "UPDATE players SET allowFriends = ? WHERE uuid_player = ?;";
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setBoolean(1, isAllowing);
            preparedStatement.setString(2, uuid.toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("An error occurred while updating friend request settings for player " + uuid + ": " + e.getMessage());
        }
    }

}