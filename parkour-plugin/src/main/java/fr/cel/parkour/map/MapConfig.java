package fr.cel.parkour.map;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.parkour.Parkour;
import fr.cel.parkour.manager.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class MapConfig {

    @NotNull private final Parkour main;
    @NotNull private final String mapName;

    @Nullable
    private final File file;
    @NotNull private final YamlConfiguration config;

    public MapConfig(@NotNull Parkour main, @NotNull String mapName) {
        this.mapName = mapName;
        this.main = main;

        this.file = new File(main.getDataFolder() + File.separator + "maps", mapName + ".yml");
        this.config = new YamlConfiguration();
    }

    /**
     * Lit le fichier de configuration sur le disque et remplit 'config'.
     * Cette méthode est sûre d'être appelée depuis un thread asynchrone.
     */
    public boolean load() {
        if (file != null && file.exists()) {
            try {
                config.load(file);
                return true;
            } catch (IOException | InvalidConfigurationException e) {
                main.getComponentLogger().error(GameManager.getPrefix().append(Component.text("Erreur dans le chargement du fichier de la carte Parkour " + this.mapName + " : " + e.getMessage(), NamedTextColor.RED)));
            }
        }
        return false;
    }

    /**
     * Construit une ParkourMap à partir de la configuration déjà chargée (config doit être prête).
     * Cette méthode doit être appelée sur le thread principal car elle utilise l'API Bukkit.
     */
    @Nullable
    public ParkourMap buildMapFromConfig() {
        if (!config.contains("displayName")) {
            main.getComponentLogger().error(GameManager.getPrefix().append(Component.text("Attention ! Un fichier n'étant pas une carte est dans le dossier 'maps'", NamedTextColor.RED)));
            return null;
        }

        String displayName = config.getString("displayName");
        Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");

        return new ParkourMap(mapName, displayName, locationSpawn, main.getGameManager());
    }

    @Nullable
    public ParkourMap getMap() {
        if (load()) return buildMapFromConfig();
        return null;
    }

}