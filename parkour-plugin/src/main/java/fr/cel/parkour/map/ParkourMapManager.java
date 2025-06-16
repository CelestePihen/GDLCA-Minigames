package fr.cel.parkour.map;

import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.parkour.Parkour;
import fr.cel.parkour.utils.Config;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ParkourMapManager {

    // TODO le change de répertoire
    @Getter private static ParkourMapManager mapManager;
    @Getter private final Map<String, ParkourMap> maps = new HashMap<>();

    private final Parkour main;

    public ParkourMapManager(Parkour main) {
        this.main = main;
        loadArenas();
        mapManager = this;
    }

    public ParkourMap getArenaByDisplayName(String name) {
        return maps.values().stream().filter(map -> map.getDisplayName().equals(name)).findFirst().orElse(null);
    }

    public ParkourMap getArenaByPlayer(Player player) {
        return maps.values().stream().filter(map -> map.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
    }

    public boolean isPlayerInArena(Player player) {
        return maps.values().stream().anyMatch(map -> map.isPlayerInArena(player));
    }

    public void loadArenas() {
        maps.clear();

        File folder = new File(main.getDataFolder(), "maps");
        if (!folder.exists()) folder.mkdirs();

        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                String name = file.getName().replace(".yml", "");
                maps.put(name, new Config(main, name).getArena());
            }
        }

        Bukkit.getConsoleSender().sendMessage(main.getGameManager().getPrefix() + ChatUtility.format("Chargement de " + maps.size() + " cartes Parkour ", ChatUtility.WHITE));
    }

}