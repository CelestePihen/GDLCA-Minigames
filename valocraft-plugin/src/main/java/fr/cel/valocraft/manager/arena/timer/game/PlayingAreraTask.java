package fr.cel.valocraft.manager.arena.timer.game;

import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.valocraft.manager.arena.Arena;
import fr.cel.valocraft.manager.arena.state.game.TimeOverArenaState;

public class PlayingAreraTask extends BukkitRunnable {

    private final Arena arena;
    private int timer;

    public PlayingAreraTask(Arena arena, int timer) {
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