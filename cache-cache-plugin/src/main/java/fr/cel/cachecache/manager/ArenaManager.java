package fr.cel.cachecache.manager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.arena.TemporaryHub;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.utils.Config;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;

public class ArenaManager {
    
    private final CacheCache main;

    @Getter private final Map<String, CCArena> arenas = new HashMap<>();
    @Setter @Getter private TemporaryHub temporaryHub;


    public ArenaManager(CacheCache main) {
        this.main = main;
        this.loadArenas();
    }

    public CCArena getArenaByPlayer(Player player) {
        for (CCArena arena : arenas.values()) {
            if (arena.getPlayers().contains(player.getUniqueId())) return arena;
        }
        return null;
    }

    public CCArena getArenaByDisplayName(String displayName) {
        for (CCArena arena : arenas.values()) {
            if (arena.getDisplayName().equals(displayName)) return arena;
        }
        return null;
    }

    public boolean isPlayerInArena(Player player) {
        for (CCArena arena : arenas.values()) {
            if (arena.isPlayerInArena(player)) return true;
        }
        return false;
    }

    public void loadArenas() {
        arenas.clear();
        File folder = new File(main.getDataFolder(), "arenas");
        if (!folder.exists()) folder.mkdirs();
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                String name = file.getName().replace(".yml", "");
                Config config = new Config(main, name);
                arenas.put(name, config.getArena());
                Bukkit.getConsoleSender().sendMessage(CCGameManager.getGameManager().getPrefix() + ChatUtility.format("Chargement de la map Cache-Cache " + name, ChatUtility.UtilityColor.WHITE));
            }
        }
    }

}