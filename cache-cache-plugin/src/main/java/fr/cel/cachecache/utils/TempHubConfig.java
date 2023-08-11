package fr.cel.cachecache.utils;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.arena.TemporaryHub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TempHubConfig {

    private YamlConfiguration config;
    private File file;

    private final CacheCache main;

    public TempHubConfig(CacheCache main) {
        this.main = main;
    }

    public TemporaryHub getTemporaryHub() {
        file = new File(main.getDataFolder(), "temporaryHub.yml");
        if (file.exists()) {
            config = new YamlConfiguration();
            try {
                config.load(file);
                return new TemporaryHub(
                        config.getBoolean("isActivated"),
                        getLocation(config),
                        getTemporaryArenas(),
                        CCArena.HunterMode.valueOf(config.getString("chosenHunterMode")),
                        config.getString("lastMap"),
                        this
                );
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Location getLocation(YamlConfiguration config) {
        double x = config.getDouble("location.x");
        double y = config.getDouble("location.y");
        double z = config.getDouble("location.z");

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

    private List<CCArena> getTemporaryArenas() {
        List<CCArena> list = new ArrayList<>();

        for (String str : config.getStringList("temporaryMaps")) {
            list.add(CCGameManager.getGameManager().getArenaManager().getArenas().get(str));
        }

        return list;
    }

    public void setValue(String path, Object value) {
        config.set(path, value);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
