package fr.cel.gameapi;

import fr.cel.gameapi.command.AfterReloadCommand;
import fr.cel.gameapi.command.CoinsCommand;
import fr.cel.gameapi.command.FriendsCommand;
import fr.cel.gameapi.command.password.ChangePasswordCommand;
import fr.cel.gameapi.command.password.LoginCommand;
import fr.cel.gameapi.command.password.RegisterCommand;
import fr.cel.gameapi.listeners.PlayersListener;
import fr.cel.gameapi.manager.*;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.manager.DatabaseManager;
import fr.cel.gameapi.utils.RPUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class GameAPI extends JavaPlugin {

    @Getter private static GameAPI instance;

    @Getter private final String prefix = ChatUtility.format("&6[GDLCA Minigames]&r ");

    @Getter private final List<ManagerAPI> apis = new ArrayList<>();

    @Getter private DatabaseManager database;
    @Getter private RPUtils rpUtils;

    @Getter private PlayersListener playersListener;

    @Override
    public void onEnable() {
        instance = this;

        database = new DatabaseManager("jdbc:mysql://", "95.111.253.89:3307", "gdlca", "cel", "Celeste9*LOL");

        rpUtils = new RPUtils();

        this.apis.add(new InventoryManager());
        this.apis.add(new FriendsManager());
        this.apis.add(new CommandsManager());
        this.apis.add(new PlayerManager());

        this.apis.forEach(managerAPI -> {
            try {
                managerAPI.enable();
                Bukkit.getConsoleSender().sendMessage(prefix + ChatUtility.format(managerAPI.getClass().getSimpleName() + " &2&l✔"));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(prefix + ChatUtility.format(managerAPI.getClass().getSimpleName() + " §4§l✖"));
                e.printStackTrace();
            }
        });

        playersListener = new PlayersListener(this);
        registerEvents(playersListener);
        registerCommands();
    }

    @Override
    public void onDisable() {
        database.disconnect();
        this.apis.forEach(ManagerAPI::disable);
    }

    private void registerCommands() {
        CommandsManager commandsManager = this.getCommandsManager();
        commandsManager.addCommand(getCommand("coins"), new CoinsCommand());
        commandsManager.addCommand(getCommand("friends"), new FriendsCommand());
        commandsManager.addCommand(getCommand("register"), new RegisterCommand(this));
        commandsManager.addCommand(getCommand("login"), new LoginCommand(this));
        commandsManager.addCommand(getCommand("changepsw"), new ChangePasswordCommand(this));
        commandsManager.addCommand(getCommand("afterrl"), new AfterReloadCommand(this));
    }

    private Object getApi(Class<?> clazz) {
        for (ManagerAPI api : this.apis) {
            if (api.getClass().equals(clazz)) return api;
        }
        return null;
    }

    public void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public InventoryManager getInventoryManager() {
        return (InventoryManager)this.getApi(InventoryManager.class);
    }

    public FriendsManager getFriendsManager() {
        return (FriendsManager)this.getApi(FriendsManager.class);
    }

    public CommandsManager getCommandsManager() {
        return (CommandsManager)this.getApi(CommandsManager.class);
    }

    public PlayerManager getPlayerManager() {
        return (PlayerManager) this.getApi(PlayerManager.class);
    }

}