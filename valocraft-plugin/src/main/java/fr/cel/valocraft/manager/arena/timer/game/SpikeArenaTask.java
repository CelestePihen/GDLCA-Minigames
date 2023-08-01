package fr.cel.valocraft.manager.arena.timer.game;

import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.valocraft.manager.arena.state.game.TimeOverArenaState;

public class SpikeArenaTask extends BukkitRunnable {

    private final ValoArena arena;
    private int timer;

    public SpikeArenaTask(ValoArena arena, int timer) {
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