package fr.cel.cachecache.manager.arena.timer.game;

import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.cachecache.manager.Arena;
import fr.cel.cachecache.manager.arena.state.game.PlayingArenaState;

public class WaitingArenaTask extends BukkitRunnable {

    private final Arena arena;
    private int timer = 2;

    public WaitingArenaTask(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        
        if (timer <= 0) {
            cancel();
            arena.setArenaState(new PlayingArenaState(arena));
            return;
        }

        timer--;
        arena.setLevel(timer);
        
    }
    
}