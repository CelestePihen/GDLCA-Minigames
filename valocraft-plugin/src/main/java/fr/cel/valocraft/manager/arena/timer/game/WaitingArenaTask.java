package fr.cel.valocraft.manager.arena.timer.game;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.valocraft.manager.arena.state.game.PlayingArenaState;

public class WaitingArenaTask extends BukkitRunnable {

    private final ValoArena arena;
    private int timer;

    public WaitingArenaTask(ValoArena arena, int timer) {
        this.arena = arena;
        this.timer = timer;
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

        if (timer <= 5 && timer != 0) {
            arena.sendMessage("Démarre dans " + timer + getSeconds() + ".");
            arena.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }
        
    }

    private String getSeconds() {
        return timer == 1 ? " seconde" : " secondes";
    }
    
}
