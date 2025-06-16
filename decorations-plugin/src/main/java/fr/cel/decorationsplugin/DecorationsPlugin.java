package fr.cel.decorationsplugin;

import fr.cel.decorationsplugin.command.ChairCommand;
import fr.cel.decorationsplugin.command.DecorationsCommand;
import fr.cel.decorationsplugin.listener.ChairListener;
import fr.cel.decorationsplugin.listener.DecorationsListener;
import fr.cel.decorationsplugin.manager.ChairManager;
import fr.cel.decorationsplugin.manager.DecorationsManager;
import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class DecorationsPlugin extends JavaPlugin {

    @Getter private static final String prefix = ChatUtility.format("&6[Decorations]&r ");
    @Getter private static DecorationsPlugin instance;

    private DecorationsManager decorationsManager;
    private ChairManager chairManager;

    @Override
    public void onEnable() {
        instance = this;

        this.chairManager = new ChairManager(this);

        this.decorationsManager = new DecorationsManager(this);

        chairManager.loadChairs();

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        if (chairManager != null) {
            chairManager.removeAllSeatedPlayers();
            chairManager.saveChairs();
        }
    }

    private void registerCommands() {
        CommandsManager commandsManager = new CommandsManager(this);
        commandsManager.addCommand("decorations", new DecorationsCommand(decorationsManager));
        commandsManager.addCommand("chairs", new ChairCommand(chairManager));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new DecorationsListener(this), this);
        getServer().getPluginManager().registerEvents(new ChairListener(chairManager), this);
    }

}