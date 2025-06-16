package fr.cel.parkour.utils;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.parkour.Parkour;
import fr.cel.parkour.map.ParkourMap;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    private final Parkour main;
    private final YamlConfiguration config;
    private final File file;
    private final String arenaName;

    public Config(Parkour main, String arenaName) {
        this.main = main;
        this.arenaName = arenaName;

        this.file = new File(main.getDataFolder() + File.separator + "maps", arenaName + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public ParkourMap getArena() {
        String displayName = config.getString("displayName");
        Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");

        return new ParkourMap(arenaName, displayName, locationSpawn, main.getGameManager());
    }

}
