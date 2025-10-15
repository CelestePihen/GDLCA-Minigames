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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ParkourMapManager {

    @Getter private static ParkourMapManager mapManager;
    @Getter private final Map<String, ParkourMap> maps = new HashMap<>();

    private final Parkour main;

    public ParkourMapManager(Parkour main) {
        this.main = main;
        loadMaps();
        mapManager = this;
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
        if (!folder.exists()) folder.mkdirs();

        if (folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".yml")))) {
                String name = file.getName().replace(".yml", "");

                MapConfig config = new MapConfig(name, main);
                ParkourMap map = config.getMap();

                if (map != null) maps.put(name, map);
            }
        }

        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix().append(Component.text("Chargement de " + maps.size() + " cartes Parkour", NamedTextColor.YELLOW)));
    }

}