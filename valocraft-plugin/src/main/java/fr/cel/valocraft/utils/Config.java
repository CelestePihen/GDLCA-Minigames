package fr.cel.valocraft.utils;

import java.io.File;
import java.io.IOException;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.valocraft.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.ValoArena;

public class Config {

    private final ValoCraft main;

    private final String arenaName;

    public Config(ValoCraft main, String arenaName) {
        this.main = main;
        this.arenaName = arenaName;
    }

    public ValoArena getArena(GameManager gameManager) {
        File file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
            String displayName = config.getString("displayName");
            Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");
            Location locationBlue = LocationUtility.parseConfigToLoc(config, "locationBlue");
            Location locationRed = LocationUtility.parseConfigToLoc(config, "locationRed");

            return new ValoArena(arenaName, displayName, locationSpawn, locationBlue, locationRed, gameManager);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

}