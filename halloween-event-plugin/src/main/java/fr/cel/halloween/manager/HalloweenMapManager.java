package fr.cel.halloween.manager;

import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.utils.MapConfig;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HalloweenMapManager {

    @Getter private static HalloweenMapManager mapManager;
    @Getter private final Map<String, HalloweenMap> maps = new HashMap<>();
    private final HalloweenEvent main;

    public HalloweenMapManager(HalloweenEvent main) {
        mapManager = this;
        this.main = main;
        loadMaps();
    }

    public HalloweenMap getMapByPlayer(Player player) {
        return maps.values().stream().filter(arena -> arena.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
    }

    public HalloweenMap getMapByDisplayName(String displayName) {
        return maps.values().stream().filter(arena -> arena.getDisplayName().equals(displayName)).findFirst().orElse(null);
    }

    public boolean isPlayerInMap(Player player) {
        return maps.values().stream().anyMatch(arena -> arena.isPlayerInMap(player));
    }

    public void loadMaps() {
        maps.clear();

        File folder = new File(main.getDataFolder(), "maps");
        if (!folder.exists()) folder.mkdirs();

        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                String name = file.getName().replace(".yml", "");
                maps.put(name, new MapConfig(main, name).getMap());
            }
        }

        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix().append(Component.text("Chargement de " + this.maps.size() + " cartes Halloween ")));
    }

}