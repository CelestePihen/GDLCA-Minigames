package fr.cel.valocraft.arena.timer.game;

import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.WaitingArenaState;

public class TimeOverArenaTask extends BukkitRunnable {

    private final ValoArena arena;
    private int timer;

    public TimeOverArenaTask(ValoArena arena, int timer) {
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
