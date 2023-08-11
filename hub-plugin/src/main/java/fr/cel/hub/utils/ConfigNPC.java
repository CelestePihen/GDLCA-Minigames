package fr.cel.hub.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.cel.hub.Hub;
import fr.cel.hub.manager.NPC;

public class ConfigNPC {

    private final Hub main;
    private final String name;

    public ConfigNPC(Hub main, String name) {
        this.main = main;
        this.name = name;
    }

    public NPC getNPC() {
        File file = new File(main.getDataFolder() + File.separator + "npcs", name + ".yml");
        if (file.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);
                return new NPC(config.getString("displayName"), loadLocation(config), config.getString("skin.texture"), config.getString("skin.signature"));
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Location loadLocation(YamlConfiguration config) {
        String world = config.getString("location.world");
        double x = config.getDouble("location.x");
        double y = config.getDouble("location.y");
        double z = config.getDouble("location.z");

        return new Location(Bukkit.getWorld(world), x, y, z);
    }
    
}