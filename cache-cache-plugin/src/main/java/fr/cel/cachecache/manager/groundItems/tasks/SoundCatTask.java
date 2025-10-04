package fr.cel.cachecache.manager.groundItems.tasks;

import fr.cel.cachecache.map.CCMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SoundCatTask extends BukkitRunnable {
    
    private final CCMap map;
    private int secondes = 10;

    public SoundCatTask(CCMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        if (secondes == 0) {
            cancel();
            return;
        }

        for (UUID uuid : map.getPlayers()) {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl != null && pl.getGameMode() != GameMode.SPECTATOR)
                pl.playSound(pl, Sound.ENTITY_CAT_AMBIENT, SoundCategory.AMBIENT, 3.0f, 1.0f);
        }

        secondes--;
    }

}