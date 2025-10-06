package fr.cel.pvp.arena;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.pvp.PVP;
import fr.cel.pvp.manager.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ArenaConfig {

    private final PVP main;
    private final String arenaName;
    private final File file;
    private final YamlConfiguration config;

    public ArenaConfig(PVP main, String arenaName) {
        this.main = main;
        this.arenaName = arenaName;

        this.file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
        this.config = new YamlConfiguration();
    }

    public PVPArena getArena() {
        if (file.exists()) {
            try {
                config.load(file);

                if (!config.contains("displayName")) {
                    Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix().append(Component.text("Attention ! Un fichier n'étant pas une carte est dans le dossier arenas", NamedTextColor.RED)));
                    return null;
                }

                String displayName = config.getString("displayName");
                Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");
                boolean fallDamage = config.getBoolean("fallDamage");
                boolean tridentActivated = config.getBoolean("tridentActivated");

                return new PVPArena(arenaName, displayName, locationSpawn, fallDamage, tridentActivated, main.getGameManager());
            } catch (IOException | InvalidConfigurationException e) {
                main.getLogger().severe(e.getMessage());
            }
        }

        return null;
    }

}