package fr.cel.parkour.map;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.parkour.Parkour;
import fr.cel.parkour.manager.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MapConfig {

    private final String mapName;
    private final Parkour main;

    private final YamlConfiguration config;
    private final File file;

    public MapConfig(String mapName, Parkour main) {
        this.mapName = mapName;
        this.main = main;

        this.file = new File(main.getDataFolder() + File.separator + "maps", mapName + ".yml");
        this.config = new YamlConfiguration();
    }

    public ParkourMap getMap() {
        if (file.exists()) {
            try {
                config.load(file);

                if (!config.contains("displayName")) {
                    Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix().append(Component.text("Attention ! Un fichier n'étant pas une carte est dans le dossier maps", NamedTextColor.RED)));
                    return null;
                }

                String displayName = config.getString("displayName");
                Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");

                return new ParkourMap(mapName, displayName, locationSpawn, main.getGameManager());
            } catch (IOException | InvalidConfigurationException e) {
                main.getLogger().severe(e.getMessage());
            }
        }

        return null;
    }

}