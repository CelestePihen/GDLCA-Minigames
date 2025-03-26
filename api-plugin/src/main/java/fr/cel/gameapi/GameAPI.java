package fr.cel.gameapi;

import fr.cel.gameapi.listeners.PlayersListener;
import fr.cel.gameapi.listeners.ServerListeners;
import fr.cel.gameapi.manager.*;
import fr.cel.gameapi.manager.database.DatabaseManager;
import fr.cel.gameapi.manager.database.FriendsManager;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.RPUtils;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class GameAPI extends JavaPlugin {

    @Getter private static GameAPI instance;

    @Getter private static final String prefix = ChatUtility.format("&6[GDLCA Minigames]&r ");

    private InventoryManager inventoryManager;
    private FriendsManager friendsManager;
    private PlayerManager playerManager;
    private DatabaseManager database;
    private CommandsManager commandsManager;
    private StatisticsManager statisticsManager;
    private AdvancementsManager advancementsManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        if (getServer().getOnlineMode()) {
            database = new DatabaseManager(getConfig().getString("host"), getConfig().getInt("port"), getConfig().getString("database"), getConfig().getString("username"), getConfig().getString("password"));
        } else {
            database = new DatabaseManager(getConfig().getString("host"), getConfig().getInt("port"), getConfig().getString("database_test"), getConfig().getString("username"), getConfig().getString("password"));
        }

        try {
            database.init();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().shutdown();
        }

        playerManager = new PlayerManager();

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerManager.addPlayerData(player);

            if (!player.isOp()) continue;

            player.sendMessage("\n\n\n\n");
            player.sendMessage(ChatUtility.format(ChatUtility.RED + "Attention !" + ChatUtility.WHITE +
                    " Un reload a été effectué. Faites /hub si vous êtes buggé(e)."));
        }

        friendsManager = new FriendsManager(this);

        commandsManager = new CommandsManager(this);
        commandsManager.registerCommands();

        inventoryManager = new InventoryManager(this);

        RPUtils.registerMusics();

        statisticsManager = new StatisticsManager(this);
        advancementsManager = new AdvancementsManager();

        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerListeners(), this);
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

}