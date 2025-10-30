package fr.cel.halloween.map;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.halloween.HalloweenEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapConfig {

    private YamlConfiguration config;
    private File file;

    private final HalloweenEvent main;
    private final String mapName;

    public MapConfig(HalloweenEvent main, String mapName) {
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
                        this,
                        main.getGameManager()
                );

                if (!config.contains("lastTracker")) {
                    main.getLogger().severe("[HalloweenEvent] Attention ! La valeur lastTracker n'est pas contenue dans la carte " + mapName);
                    return null;
                }

                map.setLastTracker(config.getString("lastTracker"));
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

    public List<Location> getChestLocations() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("chestLocations")) {
            String[] parts = str.split(",");
            if (parts.length < 4) continue;

            World world = Bukkit.getWorld(parts[0]);
            if (world == null) continue;

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            locations.add(new Location(world, x, y, z));
        }

        return locations;
    }

    public List<Location> getSoulLocations() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("souls")) {
            String[] parts = str.split(",");
            if (parts.length < 4) continue;

            World world = Bukkit.getWorld(parts[0]);
            if (world == null) continue;

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            locations.add(new Location(world, x, y, z));
        }

        return locations;
    }

    public List<Location> getPlayerSpawnsLocations() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("playerSpawns")) {
            String[] parts = str.split(",");
            if (parts.length < 4) continue;

            World world = Bukkit.getWorld(parts[0]);
            if (world == null) continue;

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            locations.add(new Location(world, x, y, z));
        }

        return locations;
    }

}