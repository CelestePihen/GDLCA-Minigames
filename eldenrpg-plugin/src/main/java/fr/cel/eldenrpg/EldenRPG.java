package fr.cel.eldenrpg;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.cel.eldenrpg.commands.NPCCommand;
import fr.cel.eldenrpg.commands.QuestCommand;
import fr.cel.eldenrpg.listeners.NPCListener;
import fr.cel.eldenrpg.listeners.PacketNPC;
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

    @Override
    public void onEnable() {
        eldenRPG = this;

        playerSerializationManager = new PlayerSerializationManager();
        questManager = new QuestManager();
        playerManager = new PlayerManager(this);
        npcManager = new NPCManager(this);

        registerListeners();
        registerCommands();
        registerPackets();
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
        pm.registerEvents(new NPCListener(), this);
        pm.registerEvents(new QuestListener(this), this);
    }

    /**
     * Méthode qui permet d'enregistrer les commandes
     */
    private void registerCommands() {
        new NPCCommand(this);
        new QuestCommand(this);
    }

    private void registerPackets() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        PacketNPC packetNPC = new PacketNPC();
        manager.addPacketListener(packetNPC);
    }

}
