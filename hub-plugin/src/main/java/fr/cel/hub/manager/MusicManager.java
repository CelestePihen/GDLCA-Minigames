package fr.cel.hub.manager;

import fr.cel.gameapi.GameAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Objects;

public class MusicManager {

    private final static World world = Objects.requireNonNull(Bukkit.getWorld("world"));
    private final static Location location = new Location(world, 270.5, 64, 59.5);

    private static Sound currentVanillaSound = null;
    private static String currentCustomSound = null;

    /**
     * Lance une musique vanilla du choix du DJ
     * @param sound La musique vanilla à lancer
     * @param player Le DJ
     */
    public static void startMusic(Sound sound, Player player) {
        stopMusic(player);

        currentVanillaSound = sound;

        world.playSound(location, currentVanillaSound, SoundCategory.RECORDS, 1.0f, 1.0f);
    }

    /**
     * Lance une musique custom du choix du DJ
     * @param sound La musique custom à lancer
     * @param player Le DJ
     */
    public static void startMusic(String sound, Player player) {
        stopMusic(player);

        currentCustomSound = sound;

        world.playSound(location, currentCustomSound, SoundCategory.RECORDS, 1.0f, 1.0f);
    }

    /**
     * Arrête la musique en cours
     * @param player Le DJ
     */
    public static void stopMusic(Player player) {
        if (currentVanillaSound != null) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.stopSound(currentVanillaSound, SoundCategory.RECORDS);
            }
            currentVanillaSound = null;
            player.sendMessage(GameAPI.getPrefix() + "Vous avez arrêté la musique.");
        }

        if (currentCustomSound != null)  {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.stopSound(currentCustomSound, SoundCategory.RECORDS);
            }
            currentCustomSound = null;
            player.sendMessage(GameAPI.getPrefix() + "Vous avez arrêté la musique.");
        }
    }

}