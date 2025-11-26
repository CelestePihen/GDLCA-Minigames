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
import java.sql.SQLException;

public class DatabaseManager {

    private static final String INSERT_PLAYER_SQL = "INSERT INTO players (player_uuid, name_player) VALUES (?, ?) ON CONFLICT (player_uuid) DO NOTHING;";
    private static final String INSERT_STAT_SQL_TEMPLATE = "INSERT INTO %s (player_uuid) VALUES (?) ON CONFLICT (player_uuid) DO NOTHING;";
    private static final String INSERT_WINTER_EVENT_SQL = "INSERT INTO event_winter2025 (player_uuid) VALUES (?) ON CONFLICT (player_uuid) DO NOTHING;";

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
     * Create a new account for a player in the database <br>
     * Note: The statistics and the winter event entries are created with triggers in the database
     * @param player The player that connects for the first time
     */
    public void createAccount(@NotNull Player player) {
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PLAYER_SQL)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) Bukkit.getConsoleSender().sendMessage(Component.text("Création d'un nouveau compte pour " + player.getName()).color(NamedTextColor.GREEN));
        } catch (SQLException e) {
            GameAPI.getInstance().getComponentLogger().error(Component.text("Erreur en créant un nouveau compte pour " + player.getName() + ": " + e.getMessage()));
        }
    }

}