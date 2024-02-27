package fr.cel.gameapi.manager;

import fr.cel.gameapi.GameAPI;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsManager extends ManagerAPI {

    private GameAPI main;

    @Override
    public void enable() {
        this.main = GameAPI.getInstance();
    }

    /**
     * Permet d'ajouter en ami 2 joueurs / Ne marche que si les 2 joueurs sont connectés
     * @param player Le joueur qui a fait la demande
     * @param friend Le joueur qui a reçu la demande
     */
    public void addFriend(Player player, Player friend) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "INSERT INTO friends (uuid_player, name_player, uuid_friend, name_friend) VALUES (?, ?, ?, ?)";
        try {
            connection = main.getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.setString(3, friend.getUniqueId().toString());
            preparedStatement.setString(4, friend.getName());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
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
        String ps = "DELETE FROM friends WHERE name_player = ? and name_friend = ?";
        try {
            connection = main.getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, friend.getName());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet d'avoir la liste des amis d'un joueur
     * @param player Le joueur
     * @return Retourne la liste d'amis
     */
    public List<String> getFriendsList(Player player) {
        List<String> friendsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "SELECT name_friend FROM friends WHERE name_player = ?";
        ResultSet resultSet = null;
        try {
            connection = main.getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, player.getName());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                friendsList.add(resultSet.getString("name_friend"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return friendsList;
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
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
        return getFriendsList(player).contains(target.getName());
    }

    /**
     * Permet d'avoir le nombre de joueurs d'un joueur
     * @param player Le joueur
     * @return Retourne le nombre de joueurs
     */
    public int getFriendCounter(Player player) {
        return getFriendsList(player).size();
    }

}
