package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsManager {

    private final GameAPI main;

    public FriendsManager(GameAPI main) {
        this.main = main;
    }

    /**
     * Adds two players as friends. Only works if both players are online.
     * @param player The player who sent the request
     * @param friend The player who received the request
     */
    public void addFriend(Player player, Player friend) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "INSERT INTO friends (uuid_player, uuid_friend) VALUES (?, ?);";
        try {
            connection = main.getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, friend.getUniqueId().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Error while adding friend: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                GameAPI.getInstance().getLogger().severe("Error while closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Removes a friend from a player's friends list.
     * @param player The player who wants to remove a friend
     * @param friend The friend to remove
     */
    public void removeFriend(Player player, Player friend) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "DELETE FROM friends WHERE uuid_player = ? and uuid_friend = ?;";
        try {
            connection = main.getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, friend.getUniqueId().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Error while removing friend: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                GameAPI.getInstance().getLogger().severe("Error while closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the list of UUIDs of a player's friends.
     * @param player The player
     * @return Returns a list of friends' UUIDs
     */
    public List<String> getFriendsUUIDList(Player player) {
        List<String> friendsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "SELECT uuid_friend FROM friends WHERE uuid_player = ?;";
        ResultSet resultSet = null;
        try {
            connection = main.getDatabase().getConnection();

            preparedStatement = connection.prepareStatement(ps);
            preparedStatement.setString(1, player.getUniqueId().toString());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                friendsList.add(resultSet.getString("uuid_friend"));
            }

        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Error while retrieving friends list: " + e.getMessage());
            return friendsList;
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                GameAPI.getInstance().getLogger().severe("Error while closing resources: " + e.getMessage());
            }
        }
        return friendsList;
    }

    /**
     * Checks if a player is friends with another player.
     * @param player The player to check
     * @param target The other player to check
     * @return Returns true if they are friends, false otherwise
     */
    public boolean isFriendWith(Player player, Player target) {
        return getFriendsUUIDList(player).contains(target.getUniqueId().toString());
    }

}