package fr.cel.cachecache.utils;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.gameapi.utils.LocationUtility;
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
                        LocationUtility.parseConfigToLoc(config, "locationSpawn"),
                        LocationUtility.parseConfigToLoc(config, "locationWaiting"),
                        config.getBoolean("fallDamage"),
                        main.getGameManager()
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
        for (GroundItem item : main.getGameManager().getGroundItems()) {
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

    private List<Location> getLocationGroundItems() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("locationGroundItems")) {
            locations.add(LocationUtility.parseStringToLoc(config, str));
        }

        return locations;
    }

}