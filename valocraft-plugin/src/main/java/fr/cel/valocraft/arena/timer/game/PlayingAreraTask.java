package fr.cel.valocraft.arena.timer.game;

import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.TimeOverArenaState;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayingAreraTask extends BukkitRunnable {

    private final ValoArena arena;
    private int timer = 100;

    public PlayingAreraTask(ValoArena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        if (timer <= 0) {
            cancel();
            arena.addRoundDefender();
            arena.setArenaState(new TimeOverArenaState(arena));
            return;
        }

        timer--;
        arena.setLevel(timer);
    }
    
}