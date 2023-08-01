package fr.cel.cachecache.manager.arena.timer.game;

import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.cachecache.manager.CCArena;
import fr.cel.cachecache.manager.arena.state.game.PlayingArenaState;

public class WaitingArenaTask extends BukkitRunnable {

    private final CCArena arena;
    private int timer = 2;

    public WaitingArenaTask(CCArena arena) {
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