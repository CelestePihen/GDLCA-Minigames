package fr.cel.valocraft.utils;

import java.io.File;
import java.io.IOException;

import fr.cel.valocraft.manager.ValoGameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.ValoArena;

public class Config {

    private final ValoCraft main;

    private final String arenaName;

    public Config(ValoCraft main, String arenaName) {
        this.main = main;
        this.arenaName = arenaName;
    }

    public ValoArena getArena(ValoGameManager gameManager) {
        File file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
        if (file.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);
                String displayName = config.getString("displayName");

                String locSpawn = config.getString("locationSpawn");
                Location locationSpawn = parseStringToLoc(locSpawn);

                String locBlue = config.getString("locationBlue");
                Location locationBlue = parseStringToLoc(locBlue);

                String locRed = config.getString("locationRed");
                Location locationRed = parseStringToLoc(locRed);

                return new ValoArena(arenaName, displayName, locationSpawn, locationBlue, locationRed, gameManager);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");

        double x = Double.parseDouble(parsedLoc[0]);
        double y = Double.parseDouble(parsedLoc[1]);
        double z = Double.parseDouble(parsedLoc[2]);

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

}