package fr.cel.halloween.manager;

import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.utils.Config;
import lombok.Getter;
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
        this.main = main;
        loadMaps();
        mapManager = this;
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
                maps.put(name, new Config(main, name).getMap());
                Bukkit.getConsoleSender().sendMessage(ChatUtility.format("[HalloweenEvent] ", ChatUtility.GOLD) + ChatUtility.format("Chargement de la carte Halloween " + name, ChatUtility.WHITE));
            }
        }
    }

}