package fr.cel.parkour.manager.area;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.cel.parkour.Parkour;
import fr.cel.parkour.utils.Config;
import lombok.Getter;

public class MapManager {
    
    @Getter private final List<ParkourMap> arenas = new ArrayList<>();
    private final Parkour main;

    public MapManager(Parkour main) {
        this.main = main;
        this.loadArenas();
    }

    public ParkourMap getArenaByDisplayName(String name) {
        for (ParkourMap arena : arenas) {
            if (arena.getDisplayName().equalsIgnoreCase(name)) return arena;
        }
        return null;
    }

    public ParkourMap getArenaByPlayer(Player player) {
        for (ParkourMap arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) return arena;
        }
        return null;
    }

    public boolean isPlayerInArena(Player player) {
        for (ParkourMap arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) return true;
        }
        return false;
    }

    public void loadArenas() {
        arenas.clear();
        File folder = new File(main.getDataFolder(), "maps");
        if (!folder.exists()) folder.mkdirs();
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                Config config = new Config(main, file.getName().replace(".yml", ""));
                arenas.add(config.getArena());
                Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[Parkour] &fChargement de la zone Parkour ") + file.getName().replace(".yml", ""));
            }
        }
    }

}