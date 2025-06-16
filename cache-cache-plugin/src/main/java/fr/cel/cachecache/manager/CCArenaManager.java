package fr.cel.cachecache.manager;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.TemporaryHub;
import fr.cel.cachecache.utils.Config;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CCArenaManager {

    @Getter private static CCArenaManager arenaManager;

    // TODO: arenas -> maps
    @Getter private final Map<String, CCArena> arenas = new HashMap<>();
    private final CacheCache main;

    @Setter @Getter private TemporaryHub temporaryHub;

    public CCArenaManager(CacheCache main) {
        this.main = main;
        loadArenas();
        arenaManager = this;
    }

    public CCArena getArenaByPlayer(Player player) {
        return arenas.values().stream().filter(arena -> arena.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
    }

    public CCArena getArenaByDisplayName(String displayName) {
        return arenas.values().stream().filter(arena -> arena.getDisplayName().equals(displayName)).findFirst().orElse(null);
    }

    public boolean isPlayerInArena(Player player) {
        return arenas.values().stream().anyMatch(arena -> arena.isPlayerInArena(player));
    }

    public void loadArenas() {
        arenas.clear();
        File folder = new File(main.getDataFolder(), "arenas");
        if (!folder.exists()) folder.mkdirs();

        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                String name = file.getName().replace(".yml", "");
                arenas.put(name, new Config(main, name).getArena());
            }
        }

        Bukkit.getConsoleSender().sendMessage(main.getGameManager().getPrefix() + ChatUtility.format("Chargement de " + this.arenas.size() + " maps Cache-Cache ", ChatUtility.WHITE));
    }

}