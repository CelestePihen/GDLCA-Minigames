package fr.cel.parkour.manager;

import fr.cel.parkour.Parkour;
import fr.cel.parkour.map.MapConfig;
import fr.cel.parkour.map.ParkourMap;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class ParkourMapManager {

    @Getter private static ParkourMapManager mapManager;
    @Getter private final Map<String, ParkourMap> maps = new HashMap<>();

    private final Parkour main;

    public ParkourMapManager(Parkour main) {
        this.main = main;
        mapManager = this;
        loadMaps();
    }

    public ParkourMap getMapByDisplayName(String name) {
        return maps.values().stream().filter(map -> map.getDisplayName().equals(name)).findFirst().orElse(null);
    }

    public ParkourMap getMapByPlayer(Player player) {
        return maps.values().stream().filter(map -> map.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
    }

    public boolean isPlayerInMap(Player player) {
        return maps.values().stream().anyMatch(map -> map.isPlayerInMap(player));
    }

    public void loadMaps() {
        maps.clear();

        File folder = new File(main.getDataFolder(), "maps");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                main.getComponentLogger().error(Component.text("Impossible de créer le dossier 'maps' dans le dataFolder", NamedTextColor.RED));
                return;
            }
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            main.getComponentLogger().info(Component.text("Aucune carte Parkour trouvée dans le dossier 'maps'", NamedTextColor.YELLOW));
            return;
        }

        List<MapConfig> loadedConfigs = new ArrayList<>();
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            for (File file : files) {
                String mapName = file.getName().replace(".yml", "");
                MapConfig config = new MapConfig(main, mapName);
                if (config.load()) loadedConfigs.add(config);
            }

            Bukkit.getScheduler().runTask(main, () -> {
                for (MapConfig config : loadedConfigs) {
                    ParkourMap map = config.buildMapFromConfig();
                    if (map != null) maps.put(map.getMapName(), map);
                }

                main.getComponentLogger().info(Component.text("Chargement de " + this.maps.size() + " cartes Parkour ", NamedTextColor.YELLOW));
            });
        });
    }

}