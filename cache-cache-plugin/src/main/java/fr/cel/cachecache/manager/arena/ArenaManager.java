package fr.cel.cachecache.manager.arena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.CCArena;
import fr.cel.cachecache.utils.Config;
import fr.cel.hub.utils.ChatUtility;
import lombok.Getter;

public class ArenaManager {
    
    private final CacheCache main;
    @Getter private List<CCArena> arenas = new ArrayList<>();

    public ArenaManager(CacheCache main) {
        this.main = main;
        this.loadArenas();
    }

    public CCArena getArenaByDisplayName(String name) {
        for (CCArena arena : arenas) {
            if (arena.getDisplayName().equalsIgnoreCase(name)) return arena;
        }
        return null;
    }

    public CCArena getArenaByName(String name) {
        for (CCArena arena : arenas) {
            if (arena.getNameArena().equalsIgnoreCase(name)) return arena;
        }
        return null;
    }

    public CCArena getArenaByPlayer(Player player) {
        for (CCArena arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) return arena;
        }
        return null;
    }

    public boolean isPlayerInArena(Player player) {
        for (CCArena arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) return true;
        }
        return false;
    }

    public void loadArenas() {
        arenas.clear();
        File folder = new File(main.getDataFolder(), "arenas");
        if (!folder.exists()) folder.mkdirs();
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                Config config = new Config(main, file.getName().replace(".yml", ""));
                arenas.add(config.getArena());
                Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[Cache-Cache] &fChargement de l'arène Cache-Cache ") + file.getName().replace(".yml", ""));
            }
        }
    }

}