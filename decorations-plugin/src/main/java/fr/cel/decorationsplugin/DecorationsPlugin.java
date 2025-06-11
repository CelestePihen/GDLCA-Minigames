package fr.cel.decorationsplugin;

import fr.cel.decorationsplugin.command.DecorationsCommand;
import fr.cel.decorationsplugin.listener.DecorationsListener;
import fr.cel.decorationsplugin.manager.DecorationsManager;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class DecorationsPlugin extends JavaPlugin {

    @Getter private static final String prefix = ChatUtility.format("&6[Decorations]&r ");
    @Getter private static DecorationsPlugin instance;

    private DecorationsManager decorationsManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.decorationsManager = new DecorationsManager(this);

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        CommandsManager commandsManager = GameAPI.getInstance().getCommandsManager();
        commandsManager.addCommand("decorations", new DecorationsCommand(decorationsManager), this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new DecorationsListener(decorationsManager), this);
    }

}
