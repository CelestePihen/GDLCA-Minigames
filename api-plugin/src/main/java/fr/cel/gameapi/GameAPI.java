package fr.cel.gameapi;

import fr.cel.gameapi.listeners.PlayersListener;
import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.gameapi.manager.InventoryManager;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.manager.database.DatabaseManager;
import fr.cel.gameapi.manager.database.FriendsManager;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.RPUtils;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.util.HashMap;

@Getter
public final class GameAPI extends JavaPlugin {

    @Getter private static GameAPI instance;

    @Getter private static final String prefix = ChatUtility.format("&6[GDLCA Minigames]&r ");

    private InventoryManager inventoryManager;
    private FriendsManager friendsManager;
    private PlayerManager playerManager;
    private DatabaseManager database;
    private CommandsManager commandsManager;
    private RPUtils rpUtils;

    @Override
    public void onEnable() {
        instance = this;

        // TODO tester localhost
        database = new DatabaseManager("95.111.253.89", 3307, "gdlca", "cel", "Celeste9*LOL");
        database.init();

        playerManager = new PlayerManager();
        friendsManager = new FriendsManager(this);

        commandsManager = new CommandsManager(this);
        commandsManager.registerCommands();

        inventoryManager = new InventoryManager(this);
        rpUtils = new RPUtils();

        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

}