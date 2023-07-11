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
import fr.cel.cachecache.manager.Arena;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.GroundItem;

public class Config {
    
    private YamlConfiguration config;
    private File file;

    private String arenaName;

    public Config(CacheCache main, String arenaName) {
        this.arenaName = arenaName;
        this.file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.load();
    }

    public Arena getArena() {
        return new Arena(
            arenaName, 
            config.getString("displayName"), 
            parseStringToLoc(config.getString("locationSpawn")), 
            parseStringToLoc(config.getString("locationWaiting")), 
            config.getInt("bestTime"), 
            config.getString("bestPlayer"), 
            Arena.HunterMode.valueOf(config.getString("hunterMode")),
            getAvailableGroundItems("availableGroundItems"),
            config.getStringList("locationGroundItems")
            );
    }

    public void setValue(String path, Object value) {
        load();
        config.set(path, value);
        save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            this.config.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private List<GroundItem> getAvailableGroundItems(String path) {
        List<GroundItem> list = new ArrayList<>();

        for (String str : config.getStringList(path)) {
            list.add(getItemByName(str));
        }

        return list;
    }

    private GroundItem getItemByName(String itemName) {
        for (GroundItem item : GameManager.getGroundItems()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    private Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");

        double x = Double.valueOf(parsedLoc[0]);
        double y = Double.valueOf(parsedLoc[1]);
        double z = Double.valueOf(parsedLoc[2]);

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

}