package fr.cel.hub;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.hub.listener.*;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.hub.commands.EventCommands;
import fr.cel.hub.commands.HubCommands;
import fr.cel.hub.manager.NPCManager;
import lombok.Getter;

public final class Hub extends JavaPlugin {

    @Getter private static Hub instance;

    @Getter private NPCManager npcManager;

    /**
     * Se déclenche quand le plugin démarre
     */
    @Override
    public void onEnable() {
        instance = this;

        npcManager = new NPCManager(this);

        registerListeners();
        registerCommands();
    }

    /**
     * Se déclenche quand le plugin s'éteint
     */
    @Override
    public void onDisable() {
        // TODO désactiver pour le moment
        // NPCManager.removeToAll();
    }

    /**
     * Fonction qui permet d'enregistrer les listeners
     */
    private void registerListeners() {
        new PlayerListener(this);
        new ChatListener(this);
        new MinigameListener(this);

        // TODO désactiver pour le moment
        // new NPCListener(this);
    }

    /**
     * Fonction qui permet d'enregistrer les commandes
     */
    private void registerCommands() {
        CommandsManager commandsManager = GameAPI.getInstance().getCommandsManager();

        commandsManager.addCommand(getCommand("event"), new EventCommands());
        commandsManager.addCommand(getCommand("hub"), new HubCommands());
        // TODO désactiver pour le moment
        // commandsManager.addCommand(getCommand("npc"), new NPCCommand(this));
    }

}