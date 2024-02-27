package fr.cel.hub.manager;

import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.hub.Hub;
import fr.cel.hub.utils.ConfigNPC;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NPCManager {
    
    @Getter private static final List<NPC> npcs = new ArrayList<>();
    private final Hub main;

    public NPCManager(Hub main) {
        this.main = main;
        loadNPCs();
    }

    private void loadNPCs() {
        main.getLogger().info("NPCs désactivés pour le moment");
//        npcs.clear();
//
//        File npcsFolder = new File(main.getDataFolder(), "npcs");
//        if (npcsFolder.mkdirs()) {
//            if (npcsFolder.isDirectory()) {
//                for (File file : npcsFolder.listFiles()) {
//                    String name = file.getName().replace(".yml", "");
//                    ConfigNPC config = new ConfigNPC(main, name);
//
//                    NPC npc = config.getNPC();
//
//                    if (npc.getLocation().getWorld() == Bukkit.getWorld("institution")) {
//                        Bukkit.getConsoleSender().sendMessage(ChatUtility.format("[Hub] &rChargement du NPC-institution " + name, ChatUtility.UtilityColor.GOLD));
//                    }
//
//                    else if (npc.getLocation().getWorld() == Bukkit.getWorld("world")) {
//                        Bukkit.getConsoleSender().sendMessage(ChatUtility.format("[Hub] &rChargement du NPC-hub " + name, ChatUtility.UtilityColor.GOLD));
//                    }
//
//                    else {
//                        Bukkit.getConsoleSender().sendMessage("&cBUG - CHARGEMENT NPC | ESSAYE DE CHARGER DANS UN MONDE INEXISTANT");
//                    }
//
//                    npc.create();
//                    npcs.add(npc);
//                    npc.showToAll();
//                }
//            }
//        }
    }

    public static void removeToAll() {
        for (NPC npc : npcs) npc.hideToAll();
    }

    public void reloadNPCs() {
        removeToAll();
        loadNPCs();
    }

}