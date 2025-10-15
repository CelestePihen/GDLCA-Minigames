package fr.cel.hub.manager.dj;

import fr.cel.gameapi.GameAPI;
import fr.cel.hub.Hub;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DJManager {

    private final Hub main;
    private final Map<String, DJMusic> musics = new HashMap<>();
    private final Map<String, DJPlaylist> playlists = new HashMap<>();

    private final World world = Bukkit.getWorlds().getFirst();
    private final Location location = new Location(world, 270.5, 64, 59.5);

    private File musicFile;
    private FileConfiguration musicConfig;

    private File playlistFile;
    private FileConfiguration playlistConfig;

    @Getter @Nullable
    private String currentCustomSound = null;

    @Getter @Nullable
    private DJPlaylist currentPlaylist = null;

    @Getter @Setter
    private DJMode currentMode = DJMode.SINGLE;

    private int currentIndex = 0;

    @Nullable
    private BukkitRunnable scheduler = null;

    @Nullable
    private DJBossBar bossBar;

    public DJManager(Hub main) {
        this.main = main;
        loadMusics();
        loadPlaylists();
    }

    // =================================================================
    // ============================ MUSIQUE ============================
    // =================================================================

    public void reloadMusics() {
        if (musicFile != null) loadMusics();
    }

    private void loadMusics() {
        musicFile = new File(main.getDataFolder(), "djmusics.yml");

        if (!musicFile.exists()) {
            try {
                musicFile.createNewFile();
                createDefaultConfig();
            } catch (IOException e) {
                main.getLogger().severe("Error when creating the djmusics.yml file : " + e.getMessage());
            }
        }

        musicConfig = YamlConfiguration.loadConfiguration(musicFile);
        ConfigurationSection section = musicConfig.getConfigurationSection("musics");

        musics.clear();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String name = section.getString(key + ".name");
                String author = section.getString(key + ".author");
                String description = section.getString(key + ".description");
                String sound = section.getString(key + ".sound");
                int durationSeconds = section.getInt(key + ".durationSeconds", 0);

                DJMusic music = new DJMusic(name, author, description, sound, durationSeconds);
                musics.put(key, music);
            }
        }

        Bukkit.getConsoleSender().sendMessage(Hub.getPrefix().append(Component.text("Chargement de " + musics.size() + " musiques DJ", NamedTextColor.YELLOW)));
    }

    private void createDefaultConfig() {
        musicConfig = new YamlConfiguration();

        musicConfig.set("musics.pigstep.name", "Pigstep");
        musicConfig.set("musics.pigstep.author", "Lena Raine");
        musicConfig.set("musics.pigstep.description", "");
        musicConfig.set("musics.pigstep.sound", "minecraft:music_disc.pigstep");

        try {
            musicConfig.save(musicFile);
        } catch (IOException e) {
            main.getLogger().severe("Error when creating the default config for djmusics file : " + e.getMessage());
        }
    }

    @Nullable
    public DJMusic getDJMusic(String musicName) {
        return musics.get(musicName);
    }

    public Collection<DJMusic> getMusics() {
        return musics.values();
    }

    // =================================================================
    // =========================== PLAYLISTS ===========================
    // =================================================================

    public void reloadPlaylists() {
        if (playlistFile != null) loadPlaylists();
    }

    private void loadPlaylists() {
        playlistFile = new File(main.getDataFolder(), "djplaylists.yml");

        if (!playlistFile.exists()) {
            try {
                playlistFile.createNewFile();
                createDefaultPlaylistConfig();
            } catch (IOException e) {
                main.getLogger().severe("Error when creating the djplaylists.yml file: " + e.getMessage());
            }
        }

        playlistConfig = YamlConfiguration.loadConfiguration(playlistFile);
        ConfigurationSection section = playlistConfig.getConfigurationSection("playlists");

        playlists.clear();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                DJPlaylist playlist = new DJPlaylist(section.getString(key + ".name", key));
                List<String> musicNames = section.getStringList(key + ".musics");
                for (String m : musicNames) {
                    DJMusic music = getDJMusic(m);
                    if (music != null) playlist.addMusic(music);
                }
                playlists.put(key.toLowerCase(), playlist);
            }
        }

        Bukkit.getConsoleSender().sendMessage(Hub.getPrefix().append(Component.text("Chargement de " + playlists.size() + " playlists DJ", NamedTextColor.YELLOW)));
    }

    private void createDefaultPlaylistConfig() {
        playlistConfig = new YamlConfiguration();
        playlistConfig.set("playlists.default", List.of("pigstep"));

        try {
            playlistConfig.save(playlistFile);
        } catch (IOException e) {
            main.getLogger().severe("Error when creating the default config for djplaylists file : " + e.getMessage());
        }
    }

    public DJPlaylist getPlaylist(String name) {
        return playlists.get(name.toLowerCase());
    }

    public Collection<DJPlaylist> getPlaylists() {
        return playlists.values();
    }

    // =================================================================
    // ======================== LECTURE MUSIQUE ========================
    // =================================================================

    public void startMusic(DJMusic music, @Nullable Player player, boolean isPlaylist) {
        if (!isPlaylist) stopMusic(player);

        if (music == null) return;
        currentCustomSound = music.customSound();
        world.playSound(location, currentCustomSound, SoundCategory.RECORDS, 1.0f, 1.0f);

        bossBar = new DJBossBar(music, location, 15);
        bossBar.start();

        // scheduler pour la prochaine musique
        if (scheduler != null) scheduler.cancel();
        scheduler = new BukkitRunnable() {
            @Override
            public void run() {
                stopCurrentMusicOnly();
                if (isPlaylist) {
                    playNextMusic(player);
                }
            }
        };
        scheduler.runTaskLater(main, music.durationSeconds() * 20L);

        if (player != null) {
            if (!isPlaylist) player.sendMessage(Hub.getPrefix().append(Component.text("Vous avez lancé la musique " + music.musicName())));
            else player.sendMessage(GameAPI.getPrefix().append(Component.text("Musique suivante...")));
        }
    }

    public void startMusic(String musicName, @Nullable Player player, boolean isPlaylist) {
        DJMusic music = getDJMusic(musicName);
        startMusic(music, player, isPlaylist);
    }

    public void startPlaylist(DJPlaylist playlist, DJMode mode, @Nullable Player player) {
        stopMusic(player);

        if (playlist == null) {
            if (player != null) player.sendMessage(Hub.getPrefix().append(Component.text("Cette playlist n'existe pas.")));
            return;
        }

        if (playlist.getMusics().isEmpty()) {
            if (player != null) player.sendMessage(Hub.getPrefix().append(Component.text("Cette playlist est vide.")));
            return;
        }

        currentPlaylist = playlist;
        currentMode = mode;
        currentIndex = 0;

        if (player != null)
            player.sendMessage(Hub.getPrefix().append(Component.text("Vous avez lancé la playlist " + playlist.getPlaylistName() + " en mode " + mode.getDisplayName())));

        playNextMusic(null);
    }

    public void playNextMusic(@Nullable Player player) {
        if (currentPlaylist == null) return;
        if (currentPlaylist.getMusics().isEmpty()) return;

        if (currentIndex >= currentPlaylist.getMusics().size()) {
            if (currentMode == DJMode.PLAYLIST_LOOP) {
                currentIndex = 0;
            } else {
                stopMusic(null);
                return;
            }
        }

        DJMusic next;
        if (currentMode == DJMode.SHUFFLE) {
            Random rand = new Random();
            next = currentPlaylist.getMusics().get(rand.nextInt(currentPlaylist.getMusics().size()));
        } else {
            next = currentPlaylist.getMusics().get(currentIndex++);
        }

        stopCurrentMusicOnly();
        startMusic(next, player, true);
    }

    private void stopCurrentMusicOnly() {
        if (scheduler != null) {
            scheduler.cancel();
            scheduler = null;
        }

        if (currentCustomSound != null) {
            for (Player pl : Bukkit.getOnlinePlayers())
                pl.stopSound(currentCustomSound, SoundCategory.RECORDS);
            currentCustomSound = null;
        }

        if (bossBar != null) {
            bossBar.removeHubPlayers();
            bossBar = null;
        }
    }


    public void stopMusic(@Nullable Player player) {
        if (scheduler != null) {
            scheduler.cancel();
            scheduler = null;
        }

        if (currentCustomSound != null) {
            for (Player pl : Bukkit.getOnlinePlayers())
                pl.stopSound(currentCustomSound, SoundCategory.RECORDS);
            currentCustomSound = null;

            if (player != null)
                player.sendMessage(Hub.getPrefix().append(Component.text("Vous avez arrêté la musique.")));
        }

        if (currentPlaylist != null) {
            currentPlaylist = null;
            currentIndex = 0;
            currentMode = DJMode.SINGLE;
        }

        if (bossBar != null) {
            bossBar.removeHubPlayers();
            bossBar = null;
        }
    }

}