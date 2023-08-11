package fr.cel.hub.manager;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.hub.utils.ConfigNPC;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NPCManager {
    
    @Getter private static List<NPC> npcs;
    private final Hub main;

    public NPCManager(Hub main) {
        this.main = main;
        npcs = new ArrayList<>();
        loadNPCs();
    }

    private void loadNPCs() {
        npcs.clear();

        File folder = new File(main.getDataFolder(), "npcs");
        if (!folder.exists()) folder.mkdirs();
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                String name = file.getName().replace(".yml", "");
                ConfigNPC config = new ConfigNPC(main, name);

                NPC npc = config.getNPC();
                
                if (npc.getLocation().getWorld() == Bukkit.getWorld("institution")) {
                    Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[Hub] &rChargement du NPC-institution " + name));
                } else if (npc.getLocation().getWorld() == Bukkit.getWorld("world")) {
                    Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[Hub] &rChargement du NPC-hub " + name));
                } else {
                    Bukkit.getConsoleSender().sendMessage("&cBUG - CHARGEMENT NPC | ESSAYE DE CHARGER DANS UN AUTRE MONDE ?");
                }

                npc.create();
                npcs.add(npc);
                npc.showToAll();
            }
        }
    }

    public static void removeToAll() {
        for (NPC npc : npcs) {
            npc.removeToAll();
        }
    }

    public void reloadNPCs() {
        removeToAll();
        loadNPCs();
    }
}