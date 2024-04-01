package fr.cel.gameapi.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;

public class DatabaseManager {

    private final String urlBase;
    private final String host;
    private final String database;
    private final String username;
    private final String password;

    private Connection connection;

    public DatabaseManager(String urlBase, String host, String database, String username, String password) {
        this.urlBase = urlBase;
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Permet d'obtenir l'instance de Connection
     */
    public Connection getConnection() {
        if (!isOnline()) {
            if (!isOnline()) {
                try {
                    connection = DriverManager.getConnection(urlBase + host + "/" + database, username, password);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return connection;
    }

    /**
     * Permet de déconnecter la base de donnée quand le serveur se ferme
     */
    public void disconnect() {
        if (isOnline()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet de savoir si la base de donnée est connecté
     * @return Retourne vrai si elle est connectée et faux si elle ne l'est pas
     */
    private boolean isOnline() {
        try {
            return (connection != null) && (!connection.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Permet de créer un nouveau compte dans la base de donnée
     * Et change son pseudo dans la base de donnée s'il l'a changé
     * @param player Le joueur qui se connecte pour la première fois
     */
    public void createAccount(Player player, String password) {
        if (!hasAccount(player)) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                connection = getConnection();
                String ps = "INSERT INTO players (uuid_player, password, name_player, coins, allowFriends) VALUES (?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(ps);

                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, player.getName());
                preparedStatement.setDouble(4, 0.0D);
                preparedStatement.setBoolean(5, true);
                preparedStatement.executeUpdate();

                Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&cCreation d'un nouveau compte pour " + player.getName()));
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&cErreur en créant un nouveau compte pour " + player.getName()));
            } finally {
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null && !connection.isClosed()) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // todo à voir
//        else {
//            Connection connection = null;
//            PreparedStatement preparedStatement = null;
//            String ps = "SELECT name_player FROM players WHERE uuid_player = ?";
//            ResultSet resultSet = null;
//            try {
//                connection = getConnection();
//                preparedStatement = connection.prepareStatement(ps);
//
//                preparedStatement.setString(1, player.getUniqueId().toString());
//
//                resultSet = preparedStatement.executeQuery();
//
//                if (resultSet.next()) {
//                    String savedName = resultSet.getString("name_player");
//                    String currentName = player.getName();
//                    if (!savedName.equalsIgnoreCase(currentName)) {
//                        String ps2 = "UPDATE players SET name_player = ? WHERE uuid_player = ?";
//                        preparedStatement = connection.prepareStatement(ps2);
//                        preparedStatement.setString(1, currentName);
//                        preparedStatement.setString(2, player.getUniqueId().toString());
//                        preparedStatement.executeUpdate();
//                    }
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (preparedStatement != null) preparedStatement.close();
//                    if (resultSet != null) resultSet.close();
//                    if (connection != null && !connection.isClosed()) connection.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    public boolean hasAccount(Player player) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ps = "SELECT coins FROM players WHERE uuid_player = ?";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, player.getUniqueId().toString());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return true;
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

}