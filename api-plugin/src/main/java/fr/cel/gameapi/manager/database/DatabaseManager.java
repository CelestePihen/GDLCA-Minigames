package fr.cel.gameapi.manager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    private HikariDataSource hikariDataSource;

    public DatabaseManager(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Initialise la Base de Données
     */
    public void init() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(600000L);
        config.setLeakDetectionThreshold(300000L);
        config.setConnectionTimeout(10000L);

        this.hikariDataSource = new HikariDataSource(config);
    }

    /**
     * Permet d'obtenir la Connection à la Base de Données et de l'initialiser la BDD si elle n'existe pas
     */
    public Connection getConnection() throws SQLException {
        if (this.hikariDataSource == null) {
            this.init();
        }

        return this.hikariDataSource.getConnection();
    }

    /**
     * Déconnecte la Base de Données
     */
    public void disconnect() {
        HikariDataSource hikariDataSource = this.hikariDataSource;
        if (hikariDataSource != null) {
            if (!hikariDataSource.isClosed()) {
                hikariDataSource.close();
            }
        }
    }

    /**
     * Créer un nouveau compte pour un joueur dans la Base de Données
     * @param player Le joueur qui se connecte pour la première fois
     */
    public void createAccount(Player player) {
        if (!hasAccount(player)) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            String ps = "INSERT INTO players (uuid_player, coins, allowFriends) VALUES (?, ?, ?)";
            try {
                connection = getConnection();
                preparedStatement = connection.prepareStatement(ps);

                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setDouble(2, 0.0D);
                preparedStatement.setBoolean(3, true);
                preparedStatement.executeUpdate();

                Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&cCréation d'un nouveau compte pour " + player.getName()));
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&cErreur en créant un nouveau compte pour " + player.getName()));
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
    }

    /**
     * Permet de vérifier si un joueur a déjà un compte dans la Base de Données
     * @param player Le joueur à vérifier
     * @return Retourne true si le joueur a déjà un compte et false s'il n'en a pas
     */
    public boolean hasAccount(Player player) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "SELECT uuid_player FROM players WHERE uuid_player = ?";
        ResultSet resultSet = null;
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