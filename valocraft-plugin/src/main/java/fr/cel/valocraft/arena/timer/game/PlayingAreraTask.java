package fr.cel.valocraft.arena.timer.game;

import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.TimeOverArenaState;

public class PlayingAreraTask extends BukkitRunnable {

    private final ValoArena arena;
    private int timer;

    public PlayingAreraTask(ValoArena arena, int timer) {
        this.arena = arena;
        this.timer = timer;
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