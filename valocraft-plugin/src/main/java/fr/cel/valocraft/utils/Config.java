package fr.cel.valocraft.utils;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private final ValoCraft main;
    private final String arenaName;

    private File file;
    private YamlConfiguration config;

    public Config(ValoCraft main, String arenaName) {
        this.main = main;
        this.arenaName = arenaName;
    }

    public ValoArena getArena(GameManager gameManager) {
        file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");

        if (file.exists()) {
            config = new YamlConfiguration();
            try {
                config.load(file);

                if (!config.contains("displayName")) {
                    Bukkit.getConsoleSender().sendMessage("§6[Valocraft] §cAttention ! Un fichier n'étant pas une arène est dans le dossier arenas");
                    return null;
                }

                String displayName = config.getString("displayName");
                Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");
                Location locationBlue = LocationUtility.parseConfigToLoc(config, "locationBlue");
                Location locationRed = LocationUtility.parseConfigToLoc(config, "locationRed");

                ValoArena arena = new ValoArena(arenaName, displayName, locationSpawn, locationBlue, locationRed, getLocationInvisibleBarrier(), gameManager);
                arena.setConfig(this);

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

    private List<Location> getLocationInvisibleBarrier() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("invisibleBarriersLocation")) {
            locations.add(LocationUtility.parseStringToLoc(config, str));
        }

        return locations;
    }

}