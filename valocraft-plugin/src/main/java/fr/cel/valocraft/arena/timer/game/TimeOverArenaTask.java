package fr.cel.valocraft.arena.timer.game;

import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.WaitingArenaState;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeOverArenaTask extends BukkitRunnable {

    private final ValoArena arena;
    private int timer = 10;

    public TimeOverArenaTask(ValoArena arena) {
        this.arena = arena;
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
