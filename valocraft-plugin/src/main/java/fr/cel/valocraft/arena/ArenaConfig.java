package fr.cel.valocraft.arena;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.valocraft.Valocraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArenaConfig {

    private final Valocraft main;
    private final String arenaName;

    private final File file;
    private final YamlConfiguration config;

    public ArenaConfig(Valocraft main, String arenaName) {
        this.main = main;
        this.arenaName = arenaName;
        this.file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
        this.config = new YamlConfiguration();
    }

    public ValoArena getArena() {
        if (file.exists()) {
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

                return new ValoArena(arenaName, displayName, locationSpawn, locationBlue, locationRed, this, main.getGameManager());
            } catch (IOException | InvalidConfigurationException e) {
                main.getLogger().severe(e.getMessage());
            }
        }

        return null;
    }

    public void setValue(String path, Object value) {
        config.set(path, value);

        try {
            config.save(file);
        } catch (IOException e) {
            main.getLogger().severe(e.getMessage());
        }
    }

    public List<Location> getLocationInvisibleBarrier() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("invisibleblocks")) {
            String[] parts = str.split(",");
            if (parts.length < 4) continue;

            World world = Bukkit.getWorld(parts[0]);
            if (world == null) continue;

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            locations.add(new Location(world, x, y, z));
        }

        return locations;
    }

}