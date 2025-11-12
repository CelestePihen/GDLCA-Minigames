package fr.cel.valocraft.manager;

import fr.cel.valocraft.Valocraft;
import fr.cel.valocraft.arena.ArenaConfig;
import fr.cel.valocraft.arena.ValoArena;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public final class ValoArenaManager {

    @Getter private static ValoArenaManager arenaManager;

    @Getter private final Map<String, ValoArena> arenas = new HashMap<>();
    @NotNull private final Valocraft main;

    public ValoArenaManager(@NotNull Valocraft main) {
        this.main = main;
        arenaManager = this;
        loadArenas();
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
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                main.getComponentLogger().error(Component.text("Impossible de créer le dossier 'arenas' dans le dataFolder", NamedTextColor.RED));
                return;
            }
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            main.getComponentLogger().info(Component.text("Aucune carte Valocraft trouvée dans le dossier 'arenas'", NamedTextColor.YELLOW));
            return;
        }

        List<ArenaConfig> loadedConfigs = new ArrayList<>();
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            for (File file : files) {
                String mapName = file.getName().replace(".yml", "");
                ArenaConfig config = new ArenaConfig(main, mapName);
                if (config.load()) loadedConfigs.add(config);
            }

            Bukkit.getScheduler().runTask(main, () -> {
                for (ArenaConfig config : loadedConfigs) {
                    ValoArena map = config.buildArenaFromConfig();
                    if (map != null) arenas.put(map.getArenaName(), map);
                }

                main.getComponentLogger().info(Component.text("Chargement de " + arenas.size() + " arènes Valocraft ", NamedTextColor.YELLOW));
            });
        });
    }

}