package fr.cel.cachecache.manager.groundItems;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.cachecache.manager.arena.CCArena;

public class SoundCatTimer extends BukkitRunnable {
    
    private final CCArena arena;
    private int secondes = 10;

    public SoundCatTimer(CCArena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        if (secondes > 0) {
            arena.getPlayers().forEach(uuid -> {
                Player pl = Bukkit.getPlayer(uuid);
                pl.playSound(pl, Sound.ENTITY_CAT_AMBIENT, SoundCategory.AMBIENT, 3.0f, 1.0f);
            });
            secondes--;
        } else {
            cancel();
        }
    }

}
