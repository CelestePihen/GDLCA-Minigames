package fr.cel.pvp.arena;

import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.pvp.PVP;
import fr.cel.pvp.utils.Config;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PVPArenaManager {

    @Getter private static PVPArenaManager arenaManager;
    @Getter private final List<PVPArena> arenas = new ArrayList<>();
    private final PVP main;

    public PVPArenaManager(PVP main) {
        arenaManager = this;
        this.main = main;
        this.loadArenas();
    }

    public PVPArena getArenaByDisplayName(String name) {
        for (PVPArena arena : arenas) {
            if (arena.getDisplayName().equalsIgnoreCase(name)) return arena;
        }
        return null;
    }

    public PVPArena getArenaByPlayer(Player player) {
        for (PVPArena arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) return arena;
        }
        return null;
    }

    public boolean isPlayerInArena(Player player) {
        for (PVPArena arena : arenas) {
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
                Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[PVP] &fChargement de l'arène PVP ") + file.getName().replace(".yml", ""));
            }
        }
    }

}