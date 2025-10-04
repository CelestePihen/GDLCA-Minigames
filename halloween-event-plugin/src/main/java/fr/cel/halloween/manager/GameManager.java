package fr.cel.halloween.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.halloween.HalloweenEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class GameManager {

    private final HalloweenEvent main;
    @Getter private static final Component prefix = Component.text("[HalloweenEvent]", NamedTextColor.GOLD).append(Component.text(" ", NamedTextColor.WHITE));

    private File soulsFile;
    private YamlConfiguration soulsConfig;

    private File playersFile;
    private YamlConfiguration playersConfig;

    private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();

    public GameManager(HalloweenEvent main) {
        this.main = main;
        loadConfigs();
    }

    public void reloadMapManager() {
        main.setHalloweenMapManager(new HalloweenMapManager(main));
    }

    private void loadConfigs() {
        // Spawn Âmes
        File soulFolder = new File(this.main.getDataFolder(), "souls");
        if (!soulFolder.exists()) soulFolder.mkdirs();

        this.soulsFile = new File(soulFolder, "souls.yml");
        if (!this.soulsFile.exists()) {
            try {
                this.soulsFile.createNewFile();
            } catch (IOException e) {
                main.getLogger().severe("Error: Creating souls file - " + e.getMessage());
            }
        }

        this.soulsConfig = YamlConfiguration.loadConfiguration(this.soulsFile);

        // Spawn Player
        File spawnPlayerFolder = new File(this.main.getDataFolder(), "spawnplayer");
        if (!spawnPlayerFolder.exists()) spawnPlayerFolder.mkdirs();

        this.playersFile = new File(spawnPlayerFolder, "spawnplayer.yml");
        if (!this.playersFile.exists()) {
            try {
                this.playersFile.createNewFile();
            } catch (IOException e) {
                main.getLogger().severe("Error: Creating spawnplayer file - " + e.getMessage());
            }
        }

        this.playersConfig = YamlConfiguration.loadConfiguration(this.playersFile);
    }

}