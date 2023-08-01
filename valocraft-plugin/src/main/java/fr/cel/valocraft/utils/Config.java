package fr.cel.valocraft.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.ValoGameManager;
import fr.cel.valocraft.manager.arena.ValoArena;

public class Config {
    
    private ValoGameManager gameManager;
    private YamlConfiguration config;
    private File file;

    private String arenaName;

    public Config(ValoCraft main, ValoGameManager gameManager, String arenaName) {
        this.gameManager = gameManager;
        this.file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.arenaName = arenaName;
        this.load();
    }

    public ValoArena getArena() {
        String displayName = this.config.getString("displayName");

        String locSpawn = this.config.getString("locationSpawn");
        Location locationSpawn = parseStringToLoc(locSpawn);

        String locBlue = this.config.getString("locationBlue");
        Location locationBlue = parseStringToLoc(locBlue);

        String locRed = this.config.getString("locationRed");
        Location locationRed = parseStringToLoc(locRed);

        ValoArena arena = new ValoArena(arenaName, displayName, locationSpawn, locationBlue, locationRed, gameManager);
        return arena;
    }

    private void load() {
        try {
            this.config.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");

        double x = Double.valueOf(parsedLoc[0]);
        double y = Double.valueOf(parsedLoc[1]);
        double z = Double.valueOf(parsedLoc[2]);

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

}
