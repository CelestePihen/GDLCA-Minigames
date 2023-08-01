package fr.cel.eldenrpg;

import com.sk89q.worldguard.WorldGuard;
import fr.cel.eldenrpg.commands.NPCCommand;
import fr.cel.eldenrpg.commands.QuestCommand;
import fr.cel.eldenrpg.listeners.NPCListener;
import fr.cel.eldenrpg.listeners.PlayerListener;
import fr.cel.eldenrpg.listeners.QuestListener;
import fr.cel.eldenrpg.manager.player.PlayerManager;
import fr.cel.eldenrpg.manager.player.PlayerSerializationManager;
import fr.cel.eldenrpg.manager.quest.QuestManager;
import fr.cel.eldenrpg.manager.npc.NPCManager;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EldenRPG extends JavaPlugin {

    @Getter private static EldenRPG eldenRPG;

    @Getter private final String prefix = "§6[EldenRPG]§r ";
    @Getter private QuestManager questManager;
    @Getter private PlayerManager playerManager;
    @Getter private NPCManager npcManager;
    @Getter private PlayerSerializationManager playerSerializationManager;
    @Getter private WorldGuard worldGuard;

    @Override
    public void onEnable() {
        eldenRPG = this;
        worldGuard = WorldGuard.getInstance();

        playerSerializationManager = new PlayerSerializationManager();
        questManager = new QuestManager(this);
        playerManager = new PlayerManager(this);
        npcManager = new NPCManager(this);

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        PlayerManager.saveDataToAll();
        NPCManager.removeToAll();
    }

    /**
     * Méthode qui permet d'enregistrer les listeners
     */
    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new NPCListener(this), this);
        pm.registerEvents(new QuestListener(this), this);
    }

    /**
     * Méthode qui permet d'enregistrer les commandes
     */
    private void registerCommands() {
        new NPCCommand(this);
        new QuestCommand(this);
    }

}
