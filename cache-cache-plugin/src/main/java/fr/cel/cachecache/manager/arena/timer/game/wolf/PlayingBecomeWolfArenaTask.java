package fr.cel.cachecache.manager.arena.timer.game.wolf;

import fr.cel.cachecache.manager.arena.CCArena;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayingBecomeWolfArenaTask extends BukkitRunnable {

    private final CCArena arena;

    public PlayingBecomeWolfArenaTask(CCArena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        UUID uuid = arena.getSeekers().get(0);

        int currentTime = arena.getWolfTimer().get(uuid);
        arena.getWolfTimer().put(uuid, currentTime + 1);

        Bukkit.getPlayer(uuid).setLevel(currentTime);
    }

}
