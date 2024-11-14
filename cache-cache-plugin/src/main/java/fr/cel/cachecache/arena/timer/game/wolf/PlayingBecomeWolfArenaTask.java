package fr.cel.cachecache.arena.timer.game.wolf;

import fr.cel.cachecache.arena.CCArena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayingBecomeWolfArenaTask extends BukkitRunnable {

    private final CCArena arena;

    public PlayingBecomeWolfArenaTask(CCArena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        UUID uuid = arena.getSeekers().getFirst();

        int currentTime = arena.getWolfTimer().get(uuid);
        arena.getWolfTimer().put(uuid, currentTime + 1);

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.setLevel(currentTime);
    }

}