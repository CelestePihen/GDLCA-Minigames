package fr.cel.hub.manager;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ConfigNPC;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                    Bukkit.getConsoleSender().sendMessage(Component.text("[Hub] ", NamedTextColor.GOLD)
                            .append(Component.text("Chargement du NPC-institution " + name, NamedTextColor.WHITE)));
                } else if (npc.getLocation().getWorld() == Bukkit.getWorld("world")) {
                    Bukkit.getConsoleSender().sendMessage(Component.text("[Hub] ", NamedTextColor.GOLD)
                            .append(Component.text("Chargement du NPC-hub " + name, NamedTextColor.WHITE)));
                } else {
                    Bukkit.getConsoleSender().sendMessage(Component.text("BUG - CHARGEMENT NPC | ESSAYE DE CHARGER DANS UN AUTRE MONDE ?", NamedTextColor.RED));
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