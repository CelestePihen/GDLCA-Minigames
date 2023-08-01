package fr.cel.hub;

import org.bukkit.WorldCreator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.hub.commands.EventCommands;
import fr.cel.hub.commands.HubCommands;
import fr.cel.hub.commands.NPCCommand;
import fr.cel.hub.listener.ChatListener;
import fr.cel.hub.listener.EventListener;
import fr.cel.hub.listener.NPCListener;
import fr.cel.hub.listener.PlayerListener;
import fr.cel.hub.manager.NPCManager;
import fr.cel.hub.manager.PlayerManager;
import fr.cel.hub.utils.RPUtils;
import lombok.Getter;

public final class Hub extends JavaPlugin {

    @Getter private PlayerManager playerManager;
    @Getter private final String prefix = "§6[GDLCA Minigames]§r ";
    @Getter private NPCManager npcManager;
    @Getter private static Hub hub;
    @Getter private RPUtils rPUtils;

    /**
     * Quand le plugin démarre
     */
    @Override
    public void onEnable() {
        hub = this;

        WorldCreator worldCreator = new WorldCreator("institution");
        worldCreator.createWorld();

        playerManager = new PlayerManager();
        npcManager = new NPCManager(this);
        rPUtils = new RPUtils();

        registerListeners();
        registerCommands();
    }

    /**
     * Quand le plugin s'éteint
     */
    @Override
    public void onDisable() {
        NPCManager.removeToAll();
    }

    /**
     * Méthode qui permet d'enregistrer les listeners
     */
    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new NPCListener(this), this);
        pm.registerEvents(new EventListener(this), this);
        pm.registerEvents(new ChatListener(this), this);
    }

    /**
     * Méthode qui permet d'enregistrer les commandes
     */
    private void registerCommands() {
        getCommand("hub").setExecutor(new HubCommands(this));
        getCommand("event").setExecutor(new EventCommands(this));
        getCommand("npc").setExecutor(new NPCCommand(this));
    }

}