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
     * Permet d'ajouter en ami 2 joueurs / Ne marche que si les 2 joueurs sont connectés
     * @param player Le joueur qui a fait la demande
     * @param friend Le joueur qui a reçu la demande
     */
    public void addFriend(Player player, Player friend) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "INSERT INTO friends (uuid_player, uuid_friend) VALUES (?, ?)";
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
     * Permet de retirer un ami d'un joueur
     * @param player Le joueur qui veut enlever de ses amis le joueur
     * @param friend L'ami qui ne va plus être l'ami du joueur
     */
    public void removeFriend(Player player, Player friend) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "DELETE FROM friends WHERE uuid_player = ? and uuid_friend = ?";
        try {
            connection = main.getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, friend.getName());

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
     * Permet d'avoir la liste des UUID des amis d'un joueur
     * @param player Le joueur
     * @return Retourne la liste d'amis
     */
    public List<String> getFriendsUUIDList(Player player) {
        List<String> friendsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "SELECT uuid_friend FROM friends WHERE uuid_player = ?";
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
     * Permet de savoir si le joueur est ami avec un autre joueur
     * @param player Le joueur à savoir s'il est ami avec un autre joueur
     * @param target L'autre joueur à savoir s'il est ami avec ce joueur
     * @return Retourne vrai s'ils sont amis et faux s'ils ne le sont pas
     */
    public boolean isFriendWith(Player player, Player target) {
        return getFriendsUUIDList(player).contains(target.getName());
    }

}
