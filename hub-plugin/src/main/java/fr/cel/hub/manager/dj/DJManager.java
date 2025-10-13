package fr.cel.hub.manager.dj;

import fr.cel.gameapi.GameAPI;
import fr.cel.hub.Hub;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DJManager {

    private final Hub main;

    private final Map<String, DJMusic> musics = new HashMap<>();

    private final World world = Bukkit.getWorlds().getFirst();
    private final Location location = new Location(world, 270.5, 64, 59.5);

    private File configFile;
    private FileConfiguration config;

    @Getter @Nullable
    private String currentCustomSound = null;

    public DJManager(Hub main) {
        this.main = main;
    }

    public void loadMusics() {
        configFile = new File(main.getDataFolder(), "djmusics.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                createDefaultConfig();
            } catch (IOException e) {
                main.getLogger().severe("Error when creating the djmusics.yml file : " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("musics");

        musics.clear();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String name = section.getString(key + ".name");
                String author = section.getString(key + ".author");
                String description = section.getString(key + ".description");
                String sound = section.getString(key + ".sound");

                DJMusic music = new DJMusic(name, author, description, sound);
                musics.put(name, music);
            }
        }

        Bukkit.getConsoleSender().sendMessage(Hub.getPrefix().append(Component.text("Chargement de " + musics.size() + " musiques DJ")));
    }

    private void createDefaultConfig() {
        config = new YamlConfiguration();

        config.set("musics.pigstep.name", "Pigstep");
        config.set("musics.pigstep.author", "Lena Raine");
        config.set("musics.pigstep.description", "");
        config.set("musics.pigstep.sound", "minecraft:music_disc.pigstep");

        try {
            config.save(configFile);
        } catch (IOException e) {
            main.getLogger().severe("Error when creating the default config for djmusics file : " + e.getMessage());
        }
    }

    public void reloadMusics() {
        if (configFile != null) loadMusics();
    }

    /**
     * Lance une musique custom du choix du DJ
     * @param sound La musique custom à lancer
     * @param player Le DJ
     */
    public void startMusic(String sound, Player player) {
        stopMusic(player);
        currentCustomSound = sound;
        world.playSound(location, currentCustomSound, SoundCategory.RECORDS, 1.0f, 1.0f);
    }

    /**
     * Arrête la musique en cours
     * @param player Le DJ
     */
    public void stopMusic(Player player) {
        if (currentCustomSound != null)  {
            for (Player pl : Bukkit.getOnlinePlayers()) pl.stopSound(currentCustomSound, SoundCategory.RECORDS);
            currentCustomSound = null;
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Vous avez arrêté la musique.")));
        }
    }

    @Nullable
    public DJMusic getDJMusic(String musicName) {
        return this.musics.get(musicName);
    }

    public Set<Map.Entry<String, DJMusic>> getEntrySet() {
        return this.musics.entrySet();
    }

}