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
//        for (CCArena arena : arenas.values()) {
//            if (arena.getPlayers().contains(player.getUniqueId())) return arena;
//        }
//        return null;
        return arenas.values().stream().filter(arena -> arena.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
    }

    public CCArena getArenaByDisplayName(String displayName) {
//        for (CCArena arena : arenas.values()) {
//            if (arena.getDisplayName().equals(displayName)) return arena;
//        }
//        return null;
        return arenas.values().stream().filter(arena -> arena.getDisplayName().equals(displayName)).findFirst().orElse(null);
    }

    public boolean isPlayerInArena(Player player) {
//        for (CCArena arena : arenas.values()) {
//            if (arena.isPlayerInArena(player)) return true;
//        }
//        return false;
        return arenas.values().stream().anyMatch(arena -> arena.isPlayerInArena(player));
    }

    public void loadArenas() {
        arenas.clear();

        File folder = new File(main.getDataFolder(), "arenas");
        if (folder.mkdirs()) {
            Bukkit.getConsoleSender().sendMessage(CCGameManager.getGameManager().getPrefix() + "Le dossier arenas a été crée avec succès.");
        } else {
            Bukkit.getConsoleSender().sendMessage(CCGameManager.getGameManager().getPrefix() + "Echec de le création du dossier arenas.");
            return;
        }

        File[] arenaFiles = folder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (arenaFiles != null) {
            for (File file : arenaFiles) {
                String name = file.getName().replace(".yml", "");
                arenas.put(name, new Config(main, name).getArena());
                Bukkit.getConsoleSender().sendMessage(CCGameManager.getGameManager().getPrefix() + ChatUtility.format("Chargement de la map Cache-Cache " + name, ChatUtility.UtilityColor.WHITE));
            }
        }
    }

}