package fr.cel.halloween.utils;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private YamlConfiguration config;
    private File file;

    private final HalloweenEvent main;
    private final String mapName;

    public Config(HalloweenEvent main, String mapName) {
        this.main = main;
        this.mapName = mapName;
    }

    public HalloweenMap getMap() {
        file = new File(main.getDataFolder() + File.separator + "maps", mapName + ".yml");
        if (file.exists()) {
            config = new YamlConfiguration();
            try {
                config.load(file);

                if (!config.contains("displayName")) {
                    main.getLogger().severe("[HalloweenEvent] Attention ! La valeur displayName n'est pas contenue dans la carte " + mapName);
                    return null;
                }

                if (!config.contains("waitingSpawn")) {
                    main.getLogger().severe("[HalloweenEvent] Attention ! La valeur waitingSpawn n'est pas contenue dans la carte " + mapName);
                    return null;
                }

                if (!config.contains("locationSpawn")) {
                    main.getLogger().severe("[HalloweenEvent] Attention ! La valeur locationSpawn n'est pas contenue dans la carte " + mapName);
                    return null;
                }

                HalloweenMap map = new HalloweenMap(
                        mapName,
                        config.getString("displayName"),
                        LocationUtility.parseConfigToLoc(config, "waitingSpawn"),
                        LocationUtility.parseConfigToLoc(config, "locationSpawn"),
                        main.getGameManager()
                );

                map.setConfig(this);

                if (!config.contains("lastTracker")) {
                    main.getLogger().severe("[HalloweenEvent] Attention ! La valeur lastTracker n'est pas contenue dans la carte " + mapName);
                    return null;
                }

                map.setLastTracker(config.getString("lastTracker"));

                if (getChestLocations() == null) {
                    main.getLogger().severe("[HalloweenEvent] Attention ! Une erreur est survenue avec la valeur chests de la carte " + mapName);
                    return null;
                }

                map.setChestLocations(getChestLocations());
                return map;
            } catch (IOException | InvalidConfigurationException e) {
                main.getLogger().severe("Un problème est survenu avec la carte " + mapName);
                main.getLogger().severe(e.getMessage());
            }
        }

        return null;
    }

    public void setValue(String path, Object value) {
        try {
            config.set(path, value);
            config.save(file);
        } catch (IOException e) {
            main.getLogger().severe("Un problème est survenu avec la carte " + mapName + " quand la valeur " + path + " a essayé d'être modifiée !");
            main.getLogger().severe(e.getMessage());
        }
    }

    private List<Location> getChestLocations() {
        List<Location> temp = new ArrayList<>();

        if (config.contains("chests")) {
            for (String key : config.getConfigurationSection("chests").getKeys(false)) {

                int x = config.getInt("chests." + key + ".x");
                int y = config.getInt("chests." + key + ".y");
                int z = config.getInt("chests." + key + ".z");

                temp.add(new Location(Bukkit.getWorld("world"), x, y, z));
            }
            return temp;
        }

        return null;
    }

}