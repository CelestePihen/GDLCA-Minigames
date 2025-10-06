package fr.cel.decorationsplugin;

import fr.cel.decorationsplugin.command.ChairCommand;
import fr.cel.decorationsplugin.command.DecorationsCommand;
import fr.cel.decorationsplugin.listener.ChairListener;
import fr.cel.decorationsplugin.listener.DecorationsListener;
import fr.cel.decorationsplugin.manager.ChairManager;
import fr.cel.decorationsplugin.manager.DecorationsManager;
import fr.cel.gameapi.manager.CommandsManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class DecorationsPlugin extends JavaPlugin {

    @Getter private static final Component prefix = Component.text("[Decorations]", NamedTextColor.GOLD).append(Component.text(" ", NamedTextColor.WHITE));
    @Getter private static DecorationsPlugin instance;

    private DecorationsManager decorationsManager;
    private ChairManager chairManager;

    @Override
    public void onEnable() {
        instance = this;

        this.chairManager = new ChairManager(this);
        chairManager.loadChairs();

        this.decorationsManager = new DecorationsManager(this);

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