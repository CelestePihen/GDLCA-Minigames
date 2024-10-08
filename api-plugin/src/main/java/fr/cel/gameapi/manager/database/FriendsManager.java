package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        String ps = "INSERT INTO friends (uuid_player, uuid_friend) VALUES (?, ?)";
        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, friend.getUniqueId().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de retirer un ami d'un joueur
     * @param player Le joueur qui veut enlever de ses amis le joueur
     * @param friend L'ami qui ne va plus être l'ami du joueur
     */
    public void removeFriend(Player player, Player friend) {
        String ps = "DELETE FROM friends WHERE uuid_player = ? and uuid_friend = ?";
        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, friend.getUniqueId().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'avoir la liste des UUID des amis d'un joueur
     * @param player Le joueur
     * @return Retourne la liste d'amis
     */
    public List<String> getFriendsUUIDList(Player player) {
        List<String> friendsList = new ArrayList<>();
        String ps = "SELECT uuid_friend FROM friends WHERE uuid_player = ?";

        try (Connection connection = main.getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, player.getUniqueId().toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    friendsList.add(resultSet.getString("uuid_friend"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    /**
     * Permet d'avoir le nombre de joueurs d'un joueur
     * @param player Le joueur
     * @return Retourne le nombre de joueurs
     */
    public int getFriendCounter(Player player) {
        return getFriendsUUIDList(player).size();
    }

}
