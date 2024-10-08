package fr.cel.cachecache.manager;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.groundItems.*;
import fr.cel.cachecache.utils.TempHubConfig;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameManager {

    @Getter private final List<GroundItem> groundItems = new ArrayList<>();;

    private final String prefix;
    private final CacheCache main;

    private final File lampsFile;
    private final YamlConfiguration lampsConfig;

    private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();

    public GameManager(CacheCache main) {
        this.main = main;
        this.prefix = ChatUtility.format("&6[Cache-Cache] &r");

        addGroundItems();

        // Bunker
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
        // Bunker
    }

    /**
     * Permet de recharger/mettre à jour les fichiers du plugin
     */
    public void reloadArenaManager() {
        main.setCcArenaManager(new CCArenaManager(main));
    }

    /**
     *
     * Permet de recharger les fichiers des maps temporaires
     */
    public void reloadTemporaryHub() {
        main.getCcArenaManager().setTemporaryHub(new TempHubConfig(main).getTemporaryHub());
    }

    /**
     * Permet d'ajouter les items qui tomberont au sol
     */
    private void addGroundItems() {
        groundItems.add(new SpeedItem());
        groundItems.add(new BlindnessItem());
        groundItems.add(new SeePlayerItem());
        groundItems.add(new SoundItem());
        groundItems.add(new InvisibilityItem());
    }

}