package fr.cel.cachecache.arena.timer.pregame;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.state.game.WaitingArenaState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingArenaTask extends BukkitRunnable {

    private final CCArena arena;
    private int timer;

    public StartingArenaTask(CCArena arena, int timer) {
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