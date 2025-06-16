package fr.cel.valocraft.arena.timer.game;

import fr.cel.valocraft.arena.ValoArena;
import org.bukkit.scheduler.BukkitRunnable;

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
            // TODO faire explosion
            return;
        }

        timer--;
        arena.setLevel(timer);
    }
    
}