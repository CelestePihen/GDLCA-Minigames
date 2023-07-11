package fr.cel.hub.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.cel.hub.Hub;
import fr.cel.hub.manager.NPC;
import lombok.Getter;

public class ConfigNPC {

    private YamlConfiguration config;
    private File file;

    private String name;
    
    public ConfigNPC(Hub main, String name) {
        this.name = name;
        this.file = new File(main.getDataFolder() + File.separator + "npcs", name + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.load();
    }

    public NPC getNPC() {
        double x = config.getDouble("location.x");
        double y = config.getDouble("location.y");
        double z = config.getDouble("location.z");
        World world = Bukkit.getWorld(config.getString("location.world"));

        Location location = new Location(world, x, y, z);

        return new NPC(
                config.getString("displayName"),
                location,
                config.getString("skin.texture"),
                config.getString("skin.signature"));
    }

    private void load() {
        try {
            this.config.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    
}