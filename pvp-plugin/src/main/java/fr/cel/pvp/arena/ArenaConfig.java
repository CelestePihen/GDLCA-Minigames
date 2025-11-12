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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class ArenaConfig {

    @NotNull private final PVP main;
    @NotNull private final String arenaName;

    @Nullable private final File file;
    @NotNull private final YamlConfiguration config;

    public ArenaConfig(@NotNull PVP main, @NotNull String arenaName) {
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
                main.getComponentLogger().error(GameManager.getPrefix().append(Component.text("Erreur dans le chargement du fichier de la carte CC " + this.arenaName + " : " + e.getMessage(), NamedTextColor.RED)));
            }
        }
        return false;
    }

    /**
     * Construit une PVPArena à partir de la configuration déjà chargée (config doit être prête).
     * Cette méthode doit être appelée sur le thread principal car elle utilise l'API Bukkit.
     */
    @Nullable
    public PVPArena buildArenaFromConfig() {
        if (!config.contains("displayName")) {
            main.getComponentLogger().error(GameManager.getPrefix().append(Component.text("Attention ! Un fichier n'étant pas une arène est dans le dossier arenas", NamedTextColor.RED)));
            return null;
        }

        String displayName = config.getString("displayName");
        Location locationSpawn = LocationUtility.parseConfigToLoc(config, "locationSpawn");
        boolean fallDamage = config.getBoolean("fallDamage");
        boolean tridentActivated = config.getBoolean("tridentActivated");

        return new PVPArena(arenaName, displayName, locationSpawn, fallDamage, tridentActivated, main.getGameManager());
    }

    @Nullable
    public PVPArena getArena() {
        if (load()) return buildArenaFromConfig();
        return null;
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
                main.getComponentLogger().error(GameManager.getPrefix().append(Component.text("Erreur dans la sauvegarde de la carte PVP " + this.arenaName + " : " + e.getMessage(), NamedTextColor.RED)));
            }
        });
    }

}