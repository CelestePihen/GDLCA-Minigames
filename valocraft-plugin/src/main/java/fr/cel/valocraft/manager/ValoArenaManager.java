package fr.cel.valocraft.manager;

import fr.cel.valocraft.Valocraft;
import fr.cel.valocraft.arena.ArenaConfig;
import fr.cel.valocraft.arena.ValoArena;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ValoArenaManager {

    @Getter private static ValoArenaManager arenaManager;
    @Getter private final Map<String, ValoArena> arenas = new HashMap<>();
    private final Valocraft main;

    public ValoArenaManager(Valocraft main) {
        arenaManager = this;
        this.main = main;
        this.loadArenas();
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

    public void loadArenas() {
        arenas.clear();

        File folder = new File(main.getDataFolder(), "arenas");
        if (!folder.exists()) folder.mkdirs();

        if (folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".yml")))) {
                String arenaName = file.getName().replace(".yml", "");

                ArenaConfig arenaConfig = new ArenaConfig(main, arenaName);
                ValoArena arena = arenaConfig.getArena();

                if (arena != null) arenas.put(arenaName, arena);
            }
        }

        Bukkit.getConsoleSender().sendMessage(main.getGameManager().getPrefix().append(Component.text("Chargement de " + arenas.size() + " arènes Valocraft ", NamedTextColor.YELLOW)));
    }

}