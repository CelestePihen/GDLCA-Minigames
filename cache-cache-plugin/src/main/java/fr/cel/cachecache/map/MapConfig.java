package fr.cel.cachecache.map;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.gameapi.utils.LocationUtility;
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
import java.util.ArrayList;
import java.util.List;

public class MapConfig {

    @NotNull private final CacheCache main;
    @NotNull private final String mapName;

    @Nullable private final File file;
    @NotNull private final YamlConfiguration config;

    public MapConfig(@NotNull CacheCache main, @NotNull String mapName) {
        this.main = main;
        this.mapName = mapName;
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
                main.getComponentLogger().error(main.getGameManager().getPrefix().append(Component.text("Erreur dans le chargement du fichier de la carte CC " + this.mapName + " : " + e.getMessage(), NamedTextColor.RED)));
            }
        }
        return false;
    }

    /**
     * Construit une CCMap à partir de la configuration déjà chargée (config doit être prête).
     * Cette méthode doit être appelée sur le thread principal car elle utilise l'API Bukkit.
     */
    @Nullable
    public CCMap buildMapFromConfig() {
        if (!config.contains("displayName")) {
            main.getComponentLogger().error(main.getGameManager().getPrefix().append(Component.text("Attention ! Un fichier n'étant pas une carte est dans le dossier 'maps'", NamedTextColor.RED)));
            return null;
        }

        CCMap map = new CCMap(
                mapName,
                config.getString("displayName"),
                CCMap.CCMode.valueOf(config.getString("hunterMode")),
                LocationUtility.parseConfigToLoc(config, "locationSpawn"),
                LocationUtility.parseConfigToLoc(config, "locationWaiting"),
                config.getBoolean("fallDamage"),
                main.getGameManager()
        );

        map.setMapConfig(this);
        map.setBestPlayer(config.getString("bestPlayer", "Aucun"), true);
        map.setBestTimer(config.getInt("bestTime", 0), true);
        map.setLastHunter(config.getString("lastHunter", "Aucun"), true);
        map.setAvailableGroundItems(getAvailableGroundItems());
        map.setLocationGroundItems(getLocationGroundItems());
        map.getWinterUtility().setGiftLocations(getGiftLocations());
        map.getWinterUtility().setChrismasTreeDepositLocations(getChristmasTreeDepositLocations());

        return map;
    }

    @Nullable
    public CCMap getMap() {
        if (load()) return buildMapFromConfig();
        return null;
    }

    public void setValue(String path, Object value) {
        config.set(path, value);
        saveAsync();
    }

    /**
     * Sauvegarde le fichier de configuration de manière asynchrone.
     */
    public void saveAsync() {
        if (file == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                // Valider et nettoyer les données avant sauvegarde
                validateAndCleanConfig();
                config.save(file);
            } catch (IOException e) {
                main.getComponentLogger().error(main.getGameManager().getPrefix().append(Component.text("Erreur dans la sauvegarde de la carte CC " + this.mapName + " : " + e.getMessage(), NamedTextColor.RED)));
            } catch (Exception e) {
                main.getComponentLogger().error(Component.text("Erreur inattendue lors de la sauvegarde de la carte CC " + this.mapName + " : " + e.getMessage(), NamedTextColor.RED));
                main.getLogger().severe("Stack trace de l'erreur: ");
                for (StackTraceElement element : e.getStackTrace()) {
                    main.getLogger().severe("  " + element.toString());
                }
            }
        });
    }

    /**
     * Valide et nettoie les données de configuration pour éviter les erreurs de sérialisation.
     */
    private void validateAndCleanConfig() {
        List<String> keysToRemove = new ArrayList<>();

        for (String key : config.getKeys(true)) {
            Object value = config.get(key);

            // Supprimer les valeurs non sérialisables
            if (value instanceof Location) {
                main.getComponentLogger().warn(Component.text("Attention : une Location non convertie détectée à la clé '" + key + "'. Elle sera supprimée.", NamedTextColor.YELLOW));
                keysToRemove.add(key);
            }
        }

        // Supprimer les clés problématiques
        for (String key : keysToRemove) {
            config.set(key, null);
        }
    }

    public List<Location> getGiftLocations() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("gifts")) {
            String[] parts = str.split(",");
            if (parts.length < 3) continue;

            try {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double z = Double.parseDouble(parts[2]);
                locations.add(new Location(Bukkit.getWorlds().getFirst(), x, y, z));
            } catch (NumberFormatException e) {
                main.getComponentLogger().error(main.getGameManager().getPrefix().append(Component.text("Erreur lors de la lecture d'une location de cadeau dans la carte " + this.mapName, NamedTextColor.RED)));
            }
        }

        return locations;
    }

    public List<Location> getChristmasTreeDepositLocations() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("christmasTreeDeposits")) {
            String[] parts = str.split(",");
            if (parts.length < 3) continue;

            try {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double z = Double.parseDouble(parts[2]);
                locations.add(new Location(Bukkit.getWorlds().getFirst(), x, y, z));
            } catch (NumberFormatException e) {
                main.getComponentLogger().error(main.getGameManager().getPrefix().append(Component.text("Erreur lors de la lecture d'une location de dépôt de cadeaux dans la carte " + this.mapName, NamedTextColor.RED)));
            }
        }

        return locations;
    }

    private GroundItem getItemByName(String itemName) {
        for (GroundItem item : main.getGameManager().getGroundItems()) {
            if (item.getName().equalsIgnoreCase(itemName)) return item;
        }

        return null;
    }

    private List<GroundItem> getAvailableGroundItems() {
        List<GroundItem> list = new ArrayList<>();

        for (String str : config.getStringList("availableGroundItems")) list.add(getItemByName(str));

        return list;
    }

    private List<Location> getLocationGroundItems() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("locationGroundItems")) locations.add(LocationUtility.parseStringToLoc(str));

        return locations;
    }

}