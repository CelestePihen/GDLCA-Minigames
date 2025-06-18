package fr.cel.gameapi.manager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.cel.gameapi.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.database);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(600000L);
        config.setLeakDetectionThreshold(300000L);
        config.setConnectionTimeout(10000L);

        this.hikariDataSource = new HikariDataSource(config);
    }

    /**
     * Permet d'obtenir la Connection à la Base de Données
     */
    public Connection getConnection() throws SQLException {
        if (this.hikariDataSource == null) this.init();

        return this.hikariDataSource.getConnection();
    }

    /**
     * Déconnecte la Base de Données
     */
    public void disconnect() {
        HikariDataSource hikariDataSource = this.hikariDataSource;
        if (hikariDataSource != null) {
            if (!hikariDataSource.isClosed()) hikariDataSource.close();
        }
    }

    /**
     * Créer un nouveau compte pour un joueur dans la Base de Données
     * @param player Le joueur qui se connecte pour la première fois
     */
    public void createAccount(Player player) {
        if (!hasAccount(player)) {
            String ps = "INSERT INTO players (uuid_player, coins, allowFriends, name_player) VALUES (?, ?, ?, ?);";
            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setDouble(2, 0.0D);
                preparedStatement.setBoolean(3, true);
                preparedStatement.setString(4, player.getName());
                preparedStatement.executeUpdate();

                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Création d'un nouveau compte pour " + player.getName());
            } catch (SQLException e) {
                GameAPI.getInstance().getLogger().severe("Erreur en créant un nouveau compte pour " + player.getName() + ": " + e.getMessage());
            }

            createStatistics(player);
        }
    }

    private void createStatistics(Player player) {
        String ps = "INSERT INTO cc_statistics (uuid_player) VALUES (?);";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Erreur en créant les statistiques du Cache-Cache pour " + player.getName() + ": " + e.getMessage());
        }

        String ps2 = "INSERT INTO valo_statistics (uuid_player) VALUES (?);";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps2)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Erreur en créant les statistiques du Valocraft pour " + player.getName() + ": " + e.getMessage());
        }

        String ps3 = "INSERT INTO pvp_statistics (uuid_player) VALUES (?);";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps3)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Erreur en créant les statistiques du PVP pour " + player.getName() + ": " + e.getMessage());
        }

        String ps4 = "INSERT INTO parkour_statistics (uuid_player) VALUES (?);";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps4)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Erreur en créant les statistiques du Parkour pour " + player.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Permet de vérifier si un joueur a déjà un compte dans la Base de Données
     * @param player Le joueur à vérifier
     * @return Retourne true si le joueur a déjà un compte et false s'il n'en a pas
     */
    public boolean hasAccount(Player player) {
        String ps = "SELECT uuid_player FROM players WHERE uuid_player = ?;";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, player.getUniqueId().toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Erreur lors de la vérification du compte pour " + player.getName() + ": " + e.getMessage());
        }
        return false;
    }

}