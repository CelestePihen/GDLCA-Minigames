package fr.cel.valocraft.manager;

import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.ArenaConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ValoArenaManager {

    @Getter private static ValoArenaManager arenaManager;
    @Getter private final Map<String, ValoArena> arenas = new HashMap<>();
    private final ValoCraft main;

    public ValoArenaManager(ValoCraft main, GameManager gameManager) {
        arenaManager = this;
        this.main = main;
        this.loadArenas(gameManager);
    }

    public ValoArena getArenaByDisplayName(String name) {
        for (ValoArena arena : arenas.values()) {
            if (arena.getDisplayName().equalsIgnoreCase(name)) return arena;
        }
        return null;
    }

    public ValoArena getArenaByPlayer(Player player) {
        for (ValoArena arena : arenas.values()) {
            if (arena.getPlayers().contains(player.getUniqueId())) return arena;
        }
        return null;
    }

    public boolean isPlayerInArena(Player player) {
        return arenas.values().stream().anyMatch(arena -> arena.isPlayerInArena(player));
    }

    public void loadArenas(GameManager gameManager) {
        arenas.clear();

        File folder = new File(main.getDataFolder(), "arenas");
        if (!folder.exists()) folder.mkdirs();

        if (folder.isDirectory()) {
            for (File file : folder.listFiles((dir, name) -> name.endsWith(".yml"))) {
                String arenaName = file.getName().replace(".yml", "");

                ArenaConfig arenaConfig = new ArenaConfig(main, arenaName);
                ValoArena arena = arenaConfig.getArena();

                if (arena != null) arenas.put(arenaName, arena);
            }
        }

        Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[Valocraft] &fChargement de " + arenas.size() + " arènes ValoCraft "));
    }

}