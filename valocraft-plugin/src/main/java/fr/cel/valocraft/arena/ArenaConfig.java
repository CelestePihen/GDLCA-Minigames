package fr.cel.valocraft.arena;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.valocraft.Valocraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArenaConfig {

    @NotNull private final Valocraft main;
    @NotNull private final String arenaName;

    @Nullable private final File file;
    @NotNull private final YamlConfiguration config;

    public ArenaConfig(@NotNull Valocraft main, @NotNull String arenaName) {
        this.main = main;
        this.arenaName = arenaName;
        this.file = new File(main.getDataFolder() + File.separator + "arenas", arenaName + ".yml");
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
                main.getComponentLogger().error(main.getGameManager().getPrefix().append(Component.text("Erreur dans le chargement du fichier de la carte Valocraft " + this.arenaName + " : " + e.getMessage(), NamedTextColor.RED)));
            }
        }
        return false;
    }

    /**
     * Construit une ValoArena à partir de la configuration déjà chargée (config doit être prête).
     * Cette méthode doit être appelée sur le thread principal car elle utilise l'API Bukkit.
     */
    @Nullable
    public ValoArena buildArenaFromConfig() {
        if (!config.contains("displayName")) {
            main.getComponentLogger().error(main.getGameManager().getPrefix().append(Component.text("Attention ! Un fichier n'étant pas une arène est dans le dossier arenas", NamedTextColor.RED)));
            return null;
        }

        String displayName = config.getString("displayName");
        Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");
        Location locationBlue = LocationUtility.parseConfigToLoc(config, "locationBlue");
        Location locationRed = LocationUtility.parseConfigToLoc(config, "locationRed");

        return new ValoArena(arenaName, displayName, locationSpawn, locationBlue, locationRed, this, main.getGameManager());
    }

    @Nullable
    public ValoArena getArena() {
        if (load()) return buildArenaFromConfig();
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
                config.save(file);
            } catch (IOException e) {
                main.getComponentLogger().error(main.getGameManager().getPrefix().append(Component.text("Erreur dans la sauvegarde de la carte Valocraft " + this.arenaName + " : " + e.getMessage(), NamedTextColor.RED)));
            }
        });
    }

    public List<Location> getLocationInvisibleBarrier() {
        List<Location> locations = new ArrayList<>();

        for (String str : config.getStringList("invisibleblocks")) {
            String[] parts = str.split(",");
            if (parts.length < 4) continue;

            World world = Bukkit.getWorld(parts[0]);
            if (world == null) continue;

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            locations.add(new Location(world, x, y, z));
        }

        return locations;
    }

}