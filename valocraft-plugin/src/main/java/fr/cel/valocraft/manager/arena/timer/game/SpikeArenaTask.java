package fr.cel.valocraft.manager.arena.timer.game;

import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.valocraft.manager.arena.Arena;
import fr.cel.valocraft.manager.arena.state.game.TimeOverArenaState;

public class SpikeArenaTask extends BukkitRunnable {

    private final Arena arena;
    private int timer;

    public SpikeArenaTask(Arena arena, int timer) {
        this.arena = arena;
        this.timer = timer;
    }

    @Override
    public void run() {
        if (timer <= 0) {
            cancel();
            arena.addRoundSpike();
            arena.setArenaState(new TimeOverArenaState(arena));
            //TODO faire explosion
            return;
        }

        timer--;
        arena.setLevel(timer);
    }
    
}