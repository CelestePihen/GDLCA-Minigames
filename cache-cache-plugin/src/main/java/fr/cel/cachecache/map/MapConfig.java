package fr.cel.cachecache.map;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.gameapi.utils.LocationUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapConfig {

    private final CacheCache main;
    private final String mapName;

    private final YamlConfiguration config;
    private final File file;

    public MapConfig(CacheCache main, String mapName) {
        this.main = main;
        this.mapName = mapName;
        this.file = new File(main.getDataFolder() + File.separator + "maps", mapName + ".yml");
        this.config = new YamlConfiguration();
    }

    public CCMap getMap() {
        if (file.exists()) {
            try {
                config.load(file);

                if (!config.contains("displayName")) {
                    Bukkit.getConsoleSender().sendMessage(main.getGameManager().getPrefix()
                            .append(Component.text("Attention ! Un fichier n'étant pas une carte est dans le dossier maps", NamedTextColor.RED)));
                    return null;
                }

                CCMap map = new CCMap(
                        mapName,
                        config.getString("displayName"),
                        CCMap.CCMode.valueOf(config.getString("hunterMode")),
                        LocationUtility.parseConfigToLoc(config, "locationSpawn"),
                        LocationUtility.parseConfigToLoc(config, "locationWaiting"),
                        config.getBoolean("fallDamage"),
                        main.getGameManager()
                );

                map.setMapConfig(this);
                map.setBestPlayer(config.getString("bestPlayer"));
                map.setBestTimer(config.getInt("bestTime"));
                map.setLastHunter(config.getString("lastHunter"));
                map.setAvailableGroundItems(getAvailableGroundItems());
                map.setLocationGroundItems(getLocationGroundItems());
                map.setGiftLocations(getGiftLocations());

                return map;
            } catch (IOException | InvalidConfigurationException e) {
                main.getLogger().severe("Erreur dans l'obtention de la carte CC " + this.mapName + " : " + e.getMessage());
            }
        }
        return null;
    }

    public void setValue(String path, Object value) {
        config.set(path, value);

        try {
            config.save(file);
        } catch (IOException e) {
            main.getLogger().severe("Erreur dans la sauvegarde de la carte CC " + this.mapName + " : " + e.getMessage());
        }
    }

    private GroundItem getItemByName(String itemName) {
        for (GroundItem item : main.getGameManager().getGroundItems()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    private List<GroundItem> getAvailableGroundItems() {
        List<GroundItem> list = new ArrayList<>();

        for (String str : config.getStringList("availableGroundItems")) {
            list.add(getItemByName(str));
        }

        return list;
    }

    private List<Location> getLocationGroundItems() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("locationGroundItems")) {
            locations.add(LocationUtility.parseStringToLoc(str));
        }

        return locations;
    }


    public List<Location> getGiftLocations() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("gifts")) {
            String[] parts = str.split(",");
            if (parts.length < 3) continue;

            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);

            locations.add(new Location(Bukkit.getWorlds().getFirst(), x, y, z));
        }

        return locations;
    }
}