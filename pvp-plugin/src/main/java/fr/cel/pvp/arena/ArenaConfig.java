package fr.cel.pvp.arena;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.pvp.PVP;
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
                    Bukkit.getConsoleSender().sendMessage(main.getGameManager().getPrefix() + "§cAttention ! Un fichier n'étant pas une carte est dans le dossier arenas");
                    return null;
                }

                String displayName = config.getString("displayName");
                Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");
                boolean fallDamage = config.getBoolean("fallDamage");

                return new PVPArena(arenaName, displayName, locationSpawn, fallDamage, main.getGameManager());
            } catch (IOException | InvalidConfigurationException e) {
                main.getLogger().severe(e.getMessage());
            }
        }

        return null;
    }

}