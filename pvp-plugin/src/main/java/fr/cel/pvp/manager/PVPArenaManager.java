package fr.cel.pvp.manager;

import fr.cel.pvp.PVP;
import fr.cel.pvp.arena.ArenaConfig;
import fr.cel.pvp.arena.PVPArena;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class PVPArenaManager {

    @Getter private static PVPArenaManager arenaManager;
    @Getter private final Map<String, PVPArena> arenas = new HashMap<>();
    private final PVP main;

    public PVPArenaManager(PVP main) {
        arenaManager = this;
        this.main = main;
        this.loadArenas();
    }

    public PVPArena getArenaByPlayer(Player player) {
        return arenas.values().stream().filter(pvpArena -> pvpArena.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
    }

    public PVPArena getArenaByDisplayName(String displayName) {
        return arenas.values().stream().filter(pvpArena -> pvpArena.getDisplayName().equals(displayName)).findFirst().orElse(null);
    }

    public boolean isPlayerInArena(Player player) {
        return arenas.values().stream().anyMatch(pvpArena -> pvpArena.isPlayerInArena(player));
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
            main.getComponentLogger().info(Component.text("Aucune carte PVP trouvée dans le dossier 'arenas'", NamedTextColor.YELLOW));
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
                    PVPArena map = config.buildArenaFromConfig();
                    if (map != null) arenas.put(map.getArenaName(), map);
                }

                main.getComponentLogger().info(Component.text("Chargement de " + arenas.size() + " arènes Valocraft ", NamedTextColor.YELLOW));
            });
        });
    }

}