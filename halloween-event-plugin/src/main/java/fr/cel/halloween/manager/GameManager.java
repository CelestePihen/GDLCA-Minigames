package fr.cel.halloween.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.halloween.HalloweenEvent;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class GameManager {

    private final HalloweenEvent main;
    @Getter private static final String prefix = ChatUtility.format("&6[HalloweenEvent] &r");

    private final File soulsFile;
    private final YamlConfiguration soulsConfig;

    private final File playersFile;
    private final YamlConfiguration playersConfig;

    private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();

    public GameManager(HalloweenEvent main) {
        this.main = main;

        // Spawn Âmes
        File soulFolder = new File(main.getDataFolder(), "souls");
        if (!soulFolder.exists()) soulFolder.mkdirs();

        soulsFile = new File(soulFolder, "souls.yml");
        if (!soulsFile.exists()) {
            try {
                soulsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        soulsConfig = YamlConfiguration.loadConfiguration(soulsFile);
        // Spawn Âmes

        // Spawn Player
        File spawnPlayerFolder = new File(main.getDataFolder(), "spawnplayer");
        if (!spawnPlayerFolder.exists()) spawnPlayerFolder.mkdirs();

        playersFile = new File(spawnPlayerFolder, "spawnplayer.yml");
        if (!playersFile.exists()) {
            try {
                playersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        // Spawn Player
    }

    public void reloadMapManager() {
        main.setHalloweenMapManager(new HalloweenMapManager(main));
    }

}