package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsManager {

    private static final String INSERT_FRIEND_QUERY = "INSERT INTO friends (player_uuid, friend_uuid) VALUES (?, ?);";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE player_uuid = ? and friend_uuid = ?;";
    private static final String SELECT_FRIENDS_QUERY = "SELECT friend_uuid FROM friends WHERE player_uuid = ?;";
    private static final String CHECK_FRIENDSHIP_QUERY = "SELECT 1 FROM friends WHERE player_uuid = ? AND friend_uuid = ? LIMIT 1;";

    private final GameAPI main;

    // TODO: add requests in database
    @Getter private final Map<Player, Player> requestsFriends = new HashMap<>();

    public FriendsManager(GameAPI main) {
        this.main = main;
    }

    /**
     * Adds two players as friends. Only works if both players are online. <br>
     * Note: This method uses a trigger in the database to ensure bidirectional friendship.
     * @param player The player who sent the request
     * @param friend The player who received the request
     */
    public void addFriend(Player player, Player friend) {
        try (Connection connection = main.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_FRIEND_QUERY)) {

            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, friend.getUniqueId().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Error while adding friend for " + player.getName() + " to " + friend.getName() + ": " + e.getMessage()));
        }
    }

    /**
     * Removes a friend from a player's friends list. <br>
     * Note: This method uses a trigger in the database to ensure bidirectional friendship removal.
     * @param player The player who wants to remove a friend
     * @param friend The friend to remove
     */
    public void removeFriend(Player player, Player friend) {
        try (Connection connection = main.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FRIEND_QUERY)) {

            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, friend.getUniqueId().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Error while removing friend for " + player.getName() + " to " + friend.getName() + ": " + e.getMessage()));
        }
    }

    /**
     * Gets the list of UUIDs of a player's friends.
     * @param player The player
     * @return Returns a list of friends' UUIDs
     */
    public List<String> getFriendsUUIDList(Player player) {
        List<String> friendsList = new ArrayList<>();

        try (Connection connection = main.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FRIENDS_QUERY)) {
            preparedStatement.setString(1, player.getUniqueId().toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    friendsList.add(resultSet.getString("friend_uuid"));
                }
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Error while retrieving friends list for " + player.getName() + ": " + e.getMessage()));
            return friendsList;
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
        try (Connection connection = main.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_FRIENDSHIP_QUERY)) {

            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, target.getUniqueId().toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Error while checking friendship between " + player.getName() + " and " + target.getName() + ": " + e.getMessage()));
            return false;
        }
    }

}