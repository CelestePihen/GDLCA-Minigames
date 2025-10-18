package fr.cel.valocraft.arena.timer.game;

import fr.cel.valocraft.arena.ValoArena;
import org.bukkit.scheduler.BukkitRunnable;

public class SpikeArenaTask extends BukkitRunnable {

    private final ValoArena arena;
    private int timer = 45;

    public SpikeArenaTask(ValoArena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        if (timer <= 0) {
            cancel();
            arena.addSpikeRoundSpike();
            // TODO faire explosion
            return;
        }

        timer--;
        arena.setLevel(timer);
    }
    
}