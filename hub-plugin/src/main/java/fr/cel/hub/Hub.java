package fr.cel.hub;

import fr.cel.hub.listener.*;
import fr.cel.hub.manager.InventoryManager;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.hub.commands.EventCommands;
import fr.cel.hub.commands.HubCommands;
import fr.cel.hub.commands.NPCCommand;
import fr.cel.hub.manager.NPCManager;
import fr.cel.hub.manager.PlayerManager;
import fr.cel.hub.utils.RPUtils;
import lombok.Getter;

@Getter
public final class Hub extends JavaPlugin {

    private final String prefix = "§6[GDLCA Minigames]§r ";

    private PlayerManager playerManager;
    private InventoryManager inventoryManager;
    private NPCManager npcManager;
    private RPUtils rpUtils;

    @Getter private static Hub hub;

    /**
     * Quand le plugin démarre
     */
    @Override
    public void onEnable() {
        hub = this;

        WorldCreator worldCreator = new WorldCreator("institution");
        worldCreator.createWorld();

        rpUtils = new RPUtils();
        playerManager = new PlayerManager();
        inventoryManager = new InventoryManager(this);
        inventoryManager.loadInventories();
        npcManager = new NPCManager(this);

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
     * Fonction qui permet d'enregistrer les listeners
     */
    private void registerListeners() {
        new PlayerListener(this);
        new NPCListener(this);
        new ChatListener(this);
        new MiniGameListener(this);
    }

    /**
     * Fonction qui permet d'enregistrer les commandes
     */
    private void registerCommands() {
        new EventCommands(this);
        new HubCommands(this);
        new NPCCommand(this);
    }

}