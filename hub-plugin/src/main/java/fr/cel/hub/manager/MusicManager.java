package fr.cel.hub.manager;

import fr.cel.gameapi.GameAPI;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

@UtilityClass
public class MusicManager {

    private final World world = Objects.requireNonNull(Bukkit.getWorld("world"));
    private final Location location = new Location(world, 270.5, 64, 59.5);

    @Getter private String currentCustomSound = null;

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
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.stopSound(currentCustomSound, SoundCategory.RECORDS);
            }
            currentCustomSound = null;
            player.sendMessage(GameAPI.getPrefix() + "Vous avez arrêté la musique.");
        }
    }

}