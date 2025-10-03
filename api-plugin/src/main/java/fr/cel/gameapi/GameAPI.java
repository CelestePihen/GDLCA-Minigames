package fr.cel.gameapi;

import fr.cel.gameapi.command.*;
import fr.cel.gameapi.listeners.OtherListeners;
import fr.cel.gameapi.listeners.PlayersListener;
import fr.cel.gameapi.listeners.ServerListeners;
import fr.cel.gameapi.manager.AdvancementsManager;
import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.gameapi.manager.InventoryManager;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.manager.database.DatabaseManager;
import fr.cel.gameapi.manager.database.FriendsManager;
import fr.cel.gameapi.manager.database.StatisticsManager;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.RPUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class GameAPI extends JavaPlugin {

    @Getter private static GameAPI instance;

    @Getter private static final Component prefix = Component.text("[GDLCA Minigames] ", NamedTextColor.GOLD).append(Component.text(" ", NamedTextColor.WHITE));

    private InventoryManager inventoryManager;
    private FriendsManager friendsManager;
    private PlayerManager playerManager;
    private DatabaseManager database;
    private CommandsManager commandsManager;
    private StatisticsManager statisticsManager;
    private AdvancementsManager advancementsManager;

    private NPCCommand npcCommand;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        initDatabase();

        this.playerManager = new PlayerManager();

        this.friendsManager = new FriendsManager(this);

        this.npcCommand = new NPCCommand();
        this.commandsManager = new CommandsManager(this);
        registerCommands();

        this.inventoryManager = new InventoryManager(this);

        RPUtils.registerMusics();

        this.statisticsManager = new StatisticsManager(this);
        this.advancementsManager = new AdvancementsManager();

        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerListeners(), this);
        getServer().getPluginManager().registerEvents(new OtherListeners(), this);
    }

    /**
     * Init the Database with the config
     */
    private void initDatabase() {
        // détecte si le config.yml est proprement configuré
        if (!getConfig().contains("host") || !getConfig().contains("port") || !getConfig().contains("database") || !getConfig().contains("database_test") ||
                !getConfig().contains("username") || !getConfig().contains("password")) {
            getLogger().severe(ChatUtility.format("&cPlease configure the database settings in the config.yml file."));
            getServer().shutdown();
            return;
        }

        // détecte si le serveur est en online mode ou pas
        if (getServer().getOnlineMode()) {
            this.database = new DatabaseManager(getConfig().getString("host"), getConfig().getInt("port"), getConfig().getString("database"), getConfig().getString("username"), getConfig().getString("password"));
        } else {
            this.database = new DatabaseManager(getConfig().getString("host"), getConfig().getInt("port"), getConfig().getString("database_test"), getConfig().getString("username"), getConfig().getString("password"));
        }

        try {
            this.database.init();
        } catch (Exception e) {
            getLogger().severe(ChatUtility.format("&cAn error occurred while connecting to the database. Please check your configuration." + e.getMessage()));
            getServer().shutdown();
        }
    }

    /**
     * Register all commands
     */
    private void registerCommands() {
        commandsManager.addCommand("coins", new CoinsCommand(getPlayerManager()));
        commandsManager.addCommand("friends", new FriendsCommand(getFriendsManager()));
        commandsManager.addCommand("gamecompass", new GameCompassComand(getPlayerManager()));
        commandsManager.addCommand("profile", new ProfileCommand());
        commandsManager.addCommand("welcome", new WelcomeCommand(getPlayerManager()));
        commandsManager.addCommand("statistics", new StatisticsCommand());
        commandsManager.addCommand("npc", getNpcCommand());
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

}