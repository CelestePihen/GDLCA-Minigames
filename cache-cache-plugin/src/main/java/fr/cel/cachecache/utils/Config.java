package fr.cel.cachecache.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.cachecache.manager.GroundItem;

public class Config {

    private YamlConfiguration config;
    private File file;

    private final CacheCache main;
    private final String arenaName;

    public Config(CacheCache main, String arenaName) {
        this.main = main;
        this.arenaName = arenaName;
    }

    public CCArena getArena() {
        file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
        if (file.exists()) {
            config = new YamlConfiguration();
            try {
                config.load(file);
                CCArena arena = new CCArena(
                        arenaName,
                        config.getString("displayName"),
                        CCArena.HunterMode.valueOf(config.getString("hunterMode")),
                        parseStringToLoc(config.getString("locationSpawn")),
                        parseStringToLoc(config.getString("locationWaiting")),
                        config.getBoolean("fallDamage")
                );

                arena.setConfig(this);
                arena.setBestPlayer(config.getString("bestPlayer"));
                arena.setBestTimer(config.getInt("bestTime"));
                arena.setLastHunter(config.getString("lastHunter"));
                arena.setAvailableGroundItems(getAvailableGroundItems());
                arena.setLocationGroundItems(getLocationGroundItems());

                return arena;
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setValue(String path, Object value) {
        config.set(path, value);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GroundItem getItemByName(String itemName) {
        for (GroundItem item : CCGameManager.getGroundItems()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    private List<GroundItem> getAvailableGroundItems() {
        List<GroundItem> list = new ArrayList<>();

        for (String str : config.getStringList("availableGroundItems")) {
            list.add(getItemByName(str));
        }

        return list;
    }

    private Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");

        double x = Double.parseDouble(parsedLoc[0]);
        double y = Double.parseDouble(parsedLoc[1]);
        double z = Double.parseDouble(parsedLoc[2]);

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

    private List<Location> getLocationGroundItems() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("locationGroundItems")) {
            locations.add(parseStringToLoc(str));
        }
        return locations;
    }

}