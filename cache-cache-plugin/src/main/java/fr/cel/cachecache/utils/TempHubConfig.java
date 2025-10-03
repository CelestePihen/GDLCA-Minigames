package fr.cel.cachecache.utils;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.TemporaryHub;
import fr.cel.gameapi.utils.LocationUtility;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TempHubConfig {

    private final CacheCache main;
    private final File file;
    private YamlConfiguration config;

    public TempHubConfig(CacheCache main) {
        this.file = new File(main.getDataFolder(), "temporaryHub.yml");
        this.main = main;
    }

    public TemporaryHub getTemporaryHub() {
        if (file.exists()) {
            config = new YamlConfiguration();
            try {
                config.load(file);
                return new TemporaryHub(
                        config.getBoolean("isActivated"),
                        LocationUtility.parseConfigToLoc(config, "location"),
                        getTemporaryMaps(),
                        CCMap.CCMode.valueOf(config.getString("chosenHunterMode")),
                        config.getString("lastMap"),
                        this,
                        main.getGameManager()
                );
            } catch (IOException | InvalidConfigurationException e) {
                main.getLogger().severe("TempHubConfig - getTemporaryHub : " + e.getMessage());
            }
        }
        return null;
    }

    private List<CCMap> getTemporaryMaps() {
        List<CCMap> list = new ArrayList<>();

        for (String str : config.getStringList("temporaryMaps")) {
            list.add(main.getCcMapManager().getMaps().get(str));
        }

        return list;
    }

    public void setValue(String path, Object value) {
        config.set(path, value);

        try {
            config.save(file);
        } catch (IOException e) {
            main.getLogger().severe("TempHubConfig - setValue : " + e.getMessage());
        }
    }

}
