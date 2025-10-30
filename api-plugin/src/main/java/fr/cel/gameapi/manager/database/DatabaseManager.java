package fr.cel.gameapi.manager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.cel.gameapi.GameAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String INSERT_PLAYER_SQL = "INSERT INTO players (uuid_player, coins, allowFriends, name_player) VALUES (?, ?, ?, ?);";
    private static final String SELECT_PLAYER_SQL = "SELECT uuid_player FROM players WHERE uuid_player = ?;";
    private static final String INSERT_STAT_SQL_TEMPLATE = "INSERT INTO %s (uuid_player) VALUES (?);";
    private static final String INSERT_WINTER_EVENT_SQL = "INSERT INTO event_winter2025 (uuid_player) VALUES (?);";

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
     * Init the database
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
     * Return the Connection to the database
     * @return The Connection to the database
     */
    public Connection getConnection() throws SQLException {
        if (this.hikariDataSource == null) this.init();
        return this.hikariDataSource.getConnection();
    }

    /**
     * Disconnect the database
     */
    public void disconnect() {
        HikariDataSource hikariDataSource = this.hikariDataSource;
        if (hikariDataSource != null && !hikariDataSource.isClosed()) hikariDataSource.close();
    }

    /**
     * Create a new account for a player in the database
     * @param player The player that connects for the first time
     */
    public void createAccount(@NotNull Player player) {
        if (!hasAccount(player)) {
            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PLAYER_SQL)) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setDouble(2, 0.0D);
                preparedStatement.setBoolean(3, true);
                preparedStatement.setString(4, player.getName());
                preparedStatement.executeUpdate();

                Bukkit.getConsoleSender().sendMessage(Component.text("Création d'un nouveau compte pour " + player.getName()).color(NamedTextColor.GREEN));
            } catch (SQLException e) {
                GameAPI.getInstance().getLogger().severe("Erreur en créant un nouveau compte pour " + player.getName() + ": " + e.getMessage());
            }

            createStatistics(player);
            createWinterEvent2025(player);
        }
    }

    /**
     * Add the player to all statistics tables
     * @param player The player to add in the statistics
     */
    private void createStatistics(Player player) {
        String[] tables = {
                "hub_statistics",
                "cc_statistics",
                "valo_statistics",
                "pvp_statistics",
                "parkour_statistics"
        };

        try (Connection conn = getConnection()) {
            boolean initialAuto = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement ps = conn.prepareStatement(String.format(INSERT_STAT_SQL_TEMPLATE, ""))) {
                    for (String table : tables) {
                        String sql = String.format(INSERT_STAT_SQL_TEMPLATE, table);
                        try (PreparedStatement tablePs = conn.prepareStatement(sql)) {
                            tablePs.setString(1, player.getUniqueId().toString());
                            tablePs.executeUpdate();
                        }
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    GameAPI.getInstance().getLogger().severe("Rollback échoué en créant les statistiques pour " + player.getName() + ": " + e.getMessage());
                }
                GameAPI.getInstance().getLogger().severe("Erreur en créant les statistiques pour " + player.getName() + " : " + ex.getMessage());
            } finally {
                conn.setAutoCommit(initialAuto);
            }
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Erreur de connexion lors de la création des statistiques pour " + player.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Add the player to the Winter Event 2025 table
     * @param player The player to add in the Winter Event 2025
     */
    private void createWinterEvent2025(Player player) {
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_WINTER_EVENT_SQL)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            GameAPI.getInstance().getLogger().severe("Erreur en créant l'entrée Winter Event 2025 pour " + player.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Verify if the player has already an account in the database
     * @param player The player to verify
     * @return True if the player has already an account. False if not
     */
    public boolean hasAccount(Player player) {
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PLAYER_SQL)) {
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