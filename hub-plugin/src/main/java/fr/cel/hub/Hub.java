package fr.cel.hub;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.hub.commands.EventCommands;
import fr.cel.hub.commands.HubCommands;
import fr.cel.hub.listener.ChatListener;
import fr.cel.hub.listener.ItemListener;
import fr.cel.hub.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hub extends JavaPlugin {

    @Getter private static Hub instance;

    /**
     * Se déclenche quand le plugin démarre
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        registerListeners();
        registerCommands();
    }

    /**
     * Se déclenche quand le plugin s'éteint
     */
    @Override
    public void onDisable() {
    }

    /**
     * Fonction qui permet d'enregistrer les listeners
     */
    private void registerListeners() {
        new PlayerListener(this);
        new ChatListener(this);
        new ItemListener(this);
    }

    /**
     * Fonction qui permet d'enregistrer les commandes
     */
    private void registerCommands() {
        CommandsManager commandsManager = GameAPI.getInstance().getCommandsManager();

        commandsManager.addCommand("hub", new HubCommands(), this);
        commandsManager.addCommand("event", new EventCommands(), this);
    }

}