package fr.cel.gameapi.manager.npc;

import fr.cel.gameapi.GameAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Pose;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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

                String displayName = config.getString("displayName");
                if (displayName == null) {
                    GameAPI.getInstance().getLogger().severe("NPC " + name + " is missing a displayName in its configuration.");
                    return null;
                }

                return new NPC(name, Component.text(displayName), loadLocation(config), Pose.valueOf(config.getString("pose", "STANDING")));
            } catch (IOException | InvalidConfigurationException e) {
                GameAPI.getInstance().getLogger().severe("Error loading NPC configuration for " + name + ": " + e.getMessage());
            }
        }

        return null;
    }

    private @NotNull Location loadLocation(@NotNull YamlConfiguration config) {
        String worldStr = config.getString("location.world", "world");
        double x = config.getDouble("location.x", 0);
        double y = config.getDouble("location.y", 0);
        double z = config.getDouble("location.z", 0);

        World world = Bukkit.getWorld(worldStr);
        if (world == null) {
            world = Bukkit.getWorlds().getFirst();
            GameAPI.getInstance().getLogger().severe("World " + worldStr + " not found. Using default (first) world for NPC " + name + ".");
        }

        if (config.contains("location.yaw") && config.contains("location.pitch")) {
            float yaw = (float) config.getDouble("location.yaw", 90);
            float pitch = (float) config.getDouble("location.pitch", 0);
            return new Location(world, x, y, z, yaw, pitch);
        }

        return new Location(world, x, y, z);
    }

}