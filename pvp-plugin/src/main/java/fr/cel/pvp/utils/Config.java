package fr.cel.pvp.utils;

import java.io.File;

import fr.cel.gameapi.utils.LocationUtility;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.cel.pvp.PVP;
import fr.cel.pvp.arena.PVPArena;

public class Config {

    private final PVP main;
    private final YamlConfiguration config;
    private final File file;
    private final String arenaName;

    public Config(PVP main, String arenaName) {
        this.main = main;
        this.arenaName = arenaName;

        this.file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public PVPArena getArena() {
        String displayName = this.config.getString("displayName");
        Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");

        return new PVPArena(arenaName, displayName, locationSpawn, main.getGameManager());
    }

}
