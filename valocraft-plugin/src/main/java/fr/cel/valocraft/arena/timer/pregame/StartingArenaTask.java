package fr.cel.valocraft.arena.timer.pregame;

import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.WaitingArenaState;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

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
            addPlayersToBossBar();
            return;
        }

        timer--;
        arena.setLevel(timer);

        if (timer == 10 || (timer <= 5 && timer != 0)) {
            arena.sendMessage(Component.text("Démarre dans " + timer + getSeconds() + "..."));
            arena.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }

    }

    private void addPlayersToBossBar() {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.showBossBar(arena.getBossBar());
        }
    }

    private String getSeconds() {
        return timer == 1 ? " seconde" : " secondes";
    }

}