package fr.cel.valocraft.arena.timer.pregame;

import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.WaitingArenaState;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingArenaTask extends BukkitRunnable {

    private final ValoArena arena;
    private int timer = 10;

    public StartingArenaTask(ValoArena arena) {
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

        if (timer == 10 || (timer <= 5 && timer != 0)) {
            arena.sendMessage(Component.text("Démarre dans " + timer + getSeconds() + "..."));
            arena.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }

    }
    
    private String getSeconds() {
        return timer == 1 ? " seconde" : " secondes";
    }

}