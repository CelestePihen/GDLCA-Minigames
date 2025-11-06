package fr.cel.cachecache.manager;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.groundItems.*;
import fr.cel.cachecache.map.TempHubConfig;
import fr.cel.cachecache.map.TemporaryHub;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.AdvancementsManager;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.manager.database.StatisticsManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameManager {

    private final CacheCache main;
    private final Component prefix = Component.empty().append(Component.text("[Cache-Cache]", NamedTextColor.GOLD)).append(Component.text(" "));

    private final List<GroundItem> groundItems = new ArrayList<>();

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
    public void reloadMapManager() {
        main.setCcMapManager(new CCMapManager(main));
    }

    /**
     * Reload the Temporary Hub file
     */
    public void reloadTemporaryHub() {
        TemporaryHub temporaryHub = new TempHubConfig(main).getTemporaryHub();
        if (temporaryHub == null) {
            main.getLogger().severe("Le Hub Temporarire du Cache-Cache n'a pas été initialisé.");
            return;
        }

        main.getCcMapManager().setTemporaryHub(temporaryHub);
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
     * Only here for the Bunker map (for now), maybe it will arrive for other maps in the future
     */
    private void loadLampsFile() {
        File folder = new File(main.getDataFolder(), "lamps");
        if (!folder.exists()) folder.mkdirs();

        lampsFile = new File(folder, "lamps.yml");
        if (!lampsFile.exists()) {
            try {
                lampsFile.createNewFile();
            } catch (IOException e) {
                main.getLogger().severe("Le fichier de configuration lamps.yml n'a pas réussi à se créer : " + e.getMessage());
            }
        }

        lampsConfig = YamlConfiguration.loadConfiguration(lampsFile);
    }

}