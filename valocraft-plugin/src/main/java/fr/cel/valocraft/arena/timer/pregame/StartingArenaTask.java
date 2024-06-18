package fr.cel.valocraft.arena.timer.pregame;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.WaitingArenaState;

public class StartingArenaTask extends BukkitRunnable {

    private final ValoArena arena;
    private int timer;

    public StartingArenaTask(ValoArena arena, int timer) {
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

        if (timer == 10 || (timer <= 5 && timer != 0)) {
            arena.sendMessage("Démarre dans " + timer + getSeconds() + "...");
            arena.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }

    }
    
    private String getSeconds() {
        return timer == 1 ? " seconde" : " secondes";
    }

}