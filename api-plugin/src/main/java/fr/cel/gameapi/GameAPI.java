package fr.cel.gameapi;

import fr.cel.gameapi.commands.*;
import fr.cel.gameapi.commands.coins.CoinsCommand;
import fr.cel.gameapi.commands.cosmetics.CosmeticsCommand;
import fr.cel.gameapi.commands.friends.FriendsCommand;
import fr.cel.gameapi.commands.winter.WinterPointsCommand;
import fr.cel.gameapi.listeners.CosmeticListeners;
import fr.cel.gameapi.listeners.OtherListeners;
import fr.cel.gameapi.listeners.PlayersListener;
import fr.cel.gameapi.listeners.ServerListeners;
import fr.cel.gameapi.manager.AdvancementsManager;
import fr.cel.gameapi.manager.InventoryManager;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.manager.command.CommandsManager;
import fr.cel.gameapi.manager.cosmetic.CosmeticsManager;
import fr.cel.gameapi.manager.database.DatabaseManager;
import fr.cel.gameapi.manager.database.FriendsManager;
import fr.cel.gameapi.manager.database.StatisticsManager;
import fr.cel.gameapi.manager.npc.NPCManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class GameAPI extends JavaPlugin {

    @Getter private static GameAPI instance;

    @Getter private static final Component prefix = Component.empty().append(Component.text("[GDLCA Minigames]", NamedTextColor.GOLD)).append(Component.text(" "));

    private InventoryManager inventoryManager;
    private FriendsManager friendsManager;
    private PlayerManager playerManager;
    private DatabaseManager database;
    private CommandsManager commandsManager;
    private StatisticsManager statisticsManager;
    private AdvancementsManager advancementsManager;
    private CosmeticsManager cosmeticsManager;
    private NPCManager npcManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        initDatabase();

        this.playerManager = new PlayerManager();

        this.friendsManager = new FriendsManager(this);

        this.statisticsManager = new StatisticsManager(this);
        this.advancementsManager = new AdvancementsManager();
        this.cosmeticsManager = new CosmeticsManager(this);

        this.commandsManager = new CommandsManager(this);
        registerCommands();

        this.inventoryManager = new InventoryManager(this);

        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerListeners(), this);
        getServer().getPluginManager().registerEvents(new OtherListeners(), this);
        getServer().getPluginManager().registerEvents(new CosmeticListeners(this), this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            removeMannequins();
            this.npcManager = new NPCManager(this);
        }, 20L);
    }

    @Override
    public void onDisable() {
        removeMannequins();

        if (this.cosmeticsManager != null) this.cosmeticsManager.shutdown();
        if (this.database != null) database.disconnect();
    }

    /**
     * Init the Database with the config
     */
    private void initDatabase() {
        if (!getConfig().contains("host") || !getConfig().contains("port") || !getConfig().contains("database") || !getConfig().contains("database_test") ||
                !getConfig().contains("username") || !getConfig().contains("password")) {
            getComponentLogger().error(Component.text("Please configure the database settings in the config.yml file. Please fill them and restart the server."));
            return;
        }

        if (getServer().getOnlineMode()) {
            this.database = new DatabaseManager(getConfig().getString("host"), getConfig().getInt("port"), getConfig().getString("database"), getConfig().getString("username"), getConfig().getString("password"));
        } else {
            this.database = new DatabaseManager(getConfig().getString("host"), getConfig().getInt("port"), getConfig().getString("database_test"), getConfig().getString("username"), getConfig().getString("password"));
        }

        try {
            this.database.init();
        } catch (Exception e) {
            getComponentLogger().error(Component.text("An error occurred while connecting to the database. Please check your configuration and restart the server." + e.getMessage()));
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
        commandsManager.addCommand("npc", new NPCCommand(this.npcManager));
        commandsManager.addCommand("cosmetics", new CosmeticsCommand(this.cosmeticsManager));
        commandsManager.addCommand("winterpoints", new WinterPointsCommand(getPlayerManager()));
    }

    private void removeMannequins() {
        Bukkit.getWorlds().forEach(world ->
                world.getEntitiesByClass(Mannequin.class).forEach(Entity::remove));
    }

}

