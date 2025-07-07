package fr.cel.cachecache.map.timer.game.wolf;

import fr.cel.cachecache.map.CCMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayingBecomeWolfMapTask extends BukkitRunnable {

    private final CCMap map;

    public PlayingBecomeWolfMapTask(CCMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        UUID uuid = map.getSeekers().getFirst();

        int currentTime = map.getWolfTimer().get(uuid);
        map.getWolfTimer().put(uuid, currentTime + 1);

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.setLevel(currentTime);
    }

}