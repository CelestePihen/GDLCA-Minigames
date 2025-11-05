package fr.cel.cachecache.manager;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.TemporaryHub;
import fr.cel.cachecache.utils.MapConfig;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CCMapManager {

    @Getter private static CCMapManager mapManager;

    @Getter private final Map<String, CCMap> maps = new HashMap<>();
    private final CacheCache main;

    @Setter @Getter private TemporaryHub temporaryHub;

    public CCMapManager(CacheCache main) {
        this.main = main;
        loadMaps();
        mapManager = this;
    }

    public CCMap getMapByPlayer(Player player) {
        return maps.values().stream().filter(map -> map.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
    }

    public CCMap getMapByDisplayName(String displayName) {
        return maps.values().stream().filter(map -> map.getDisplayName().equals(displayName)).findFirst().orElse(null);
    }

    public boolean isPlayerInMap(Player player) {
        return maps.values().stream().anyMatch(map -> map.isPlayerInMap(player));
    }

    public void loadMaps() {
        maps.clear();

        File folder = new File(main.getDataFolder(), "maps");
        if (!folder.exists()) folder.mkdirs();

        if (folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files == null) return;

            for (File file : files) {
                String mapName = file.getName().replace(".yml", "");
                CCMap map = new MapConfig(main, mapName).getMap();
                if (map != null) maps.put(mapName, map);
            }
        }

        Bukkit.getConsoleSender().sendMessage(main.getGameManager().getPrefix().append(Component.text("Chargement de " + this.maps.size() + " maps Cache-Cache ", NamedTextColor.YELLOW)));
    }

}