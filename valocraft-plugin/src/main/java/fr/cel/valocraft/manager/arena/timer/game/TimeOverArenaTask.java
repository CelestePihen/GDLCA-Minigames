package fr.cel.valocraft.manager.arena.timer.game;

import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.valocraft.manager.arena.Arena;
import fr.cel.valocraft.manager.arena.state.game.WaitingArenaState;

public class TimeOverArenaTask extends BukkitRunnable {

    private final Arena arena;
    private int timer;

    public TimeOverArenaTask(Arena arena, int timer) {
        this.arena = arena;
        this.timer = timer;
    }

    @Override
    public void run() {
        
        if (timer <= 0) {
            cancel();
            arena.setArenaState(new WaitingArenaState(arena));
            return;
        }

        timer--;
        arena.setLevel(timer);

    }
    
}
