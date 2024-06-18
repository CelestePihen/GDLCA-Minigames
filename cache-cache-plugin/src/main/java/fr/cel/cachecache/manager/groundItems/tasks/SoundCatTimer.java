package fr.cel.cachecache.manager.groundItems.tasks;

import fr.cel.cachecache.arena.CCArena;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
                if (pl.getGameMode() == GameMode.SPECTATOR) return;
                pl.playSound(pl, Sound.ENTITY_CAT_AMBIENT, SoundCategory.AMBIENT, 3.0f, 1.0f);
            });
            secondes--;
        } else {
            cancel();
        }
    }

}
