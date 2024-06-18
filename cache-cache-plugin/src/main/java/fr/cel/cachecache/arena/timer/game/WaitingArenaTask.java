package fr.cel.cachecache.arena.timer.game;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.state.game.PlayingArenaState;
import org.bukkit.scheduler.BukkitRunnable;

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