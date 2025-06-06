package fr.cel.cachecache.manager;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.groundItems.*;
import fr.cel.cachecache.utils.TempHubConfig;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.AdvancementsManager;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.manager.database.StatisticsManager;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameManager {

    private final CacheCache main;
    private final String prefix = ChatUtility.format("&6[Cache-Cache] &r");

    private final List<GroundItem> groundItems = new ArrayList<>();;

    // Bunker map
    private File lampsFile;
    private YamlConfiguration lampsConfig;

    // API Managers
    private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();
    private final StatisticsManager statisticsManager = GameAPI.getInstance().getStatisticsManager();
    private final AdvancementsManager advancementsManager = GameAPI.getInstance().getAdvancementsManager();

    public GameManager(CacheCache main) {
        this.main = main;

        addGroundItems();
        loadLampsFile();
    }

    /**
     * Reload the map files of the Cache-Cache
     */
    public void reloadArenaManager() {
        main.setCcArenaManager(new CCArenaManager(main));
    }

    /**
     * Reload the Temporary Hub file
     */
    public void reloadTemporaryHub() {
        main.getCcArenaManager().setTemporaryHub(new TempHubConfig(main).getTemporaryHub());
    }

    /**
     * Add all GroundItems that is available in the game mode
     */
    private void addGroundItems() {
        groundItems.add(new SpeedItem());
        groundItems.add(new BlindnessItem());
        groundItems.add(new PointPlayerItem());
        groundItems.add(new SoundItem());
        groundItems.add(new InvisibilityItem());
    }

    /**
     * Load the lamps file and create it if it doesn't exist <br>
     * Only here for the Bunker map (for now)
     */
    private void loadLampsFile() {
        File folder = new File(main.getDataFolder(), "lamps");
        if (!folder.exists()) folder.mkdirs();

        lampsFile = new File(folder, "lamps.yml");
        if (!lampsFile.exists()) {
            try {
                lampsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lampsConfig = YamlConfiguration.loadConfiguration(lampsFile);
    }

}