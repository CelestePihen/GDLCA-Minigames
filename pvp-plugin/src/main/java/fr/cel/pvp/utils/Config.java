package fr.cel.pvp.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.cel.pvp.PVP;
import fr.cel.pvp.manager.arena.Arena;

public class Config {
    
    private YamlConfiguration config;
    private File file;

    private String arenaName;

    public Config(PVP main, String arenaName) {
        this.file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.arenaName = arenaName;
        this.load();
    }

    public Arena getArena() {
        String displayName = this.config.getString("displayName");

        String locSpawn = this.config.getString("locationSpawn");
        Location locationSpawn = parseStringToLoc(locSpawn);

        Arena arena = new Arena(arenaName, displayName, locationSpawn);
        return arena;
    }

    private void load() {
        try {
            this.config.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");

        return new Location(Bukkit.getWorld("world"), Double.valueOf(parsedLoc[0]), Double.valueOf(parsedLoc[1]), Double.valueOf(parsedLoc[2]));
    }

}
