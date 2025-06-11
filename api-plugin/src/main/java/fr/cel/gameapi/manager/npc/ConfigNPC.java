package fr.cel.gameapi.manager.npc;

import fr.cel.gameapi.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigNPC {

    private final JavaPlugin main;
    private final String name;

    public ConfigNPC(JavaPlugin main, String name) {
        this.main = main;
        this.name = name;
    }

    public NPC getNPC() {
        File file = new File(main.getDataFolder() + File.separator + "npcs", name + ".yml");
        if (file.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);
                return new NPC(
                        name,
                        config.getString("displayName"),
                        loadLocation(config),
                        new Skin(config.getString("skin.value"), config.getString("skin.signature"))
                );
            } catch (IOException | InvalidConfigurationException e) {
                GameAPI.getInstance().getLogger().severe("Error loading NPC configuration for " + name + ": " + e.getMessage());
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