package fr.cel.cachecache.arena.timer.game.wolf;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.state.game.PlayingArenaState;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class PlayingWolfArenaTask extends BukkitRunnable {

    private final CCArena arena;
    private String timerString;
    private final PlayingArenaState arenaState;
    private boolean wolfFree;

    public PlayingWolfArenaTask(CCArena arena, PlayingArenaState arenaState) {
        this.arena = arena;
        this.arenaState = arenaState;
        this.wolfFree = false;
    }

    @Override
    public void run() {
        if (arena.getTimer() <= 0) {
            cancel();
            arena.checkWinOrEndGame();
            return;
        }

        arena.setTimer(arena.getTimer() - 1);

        int minutes = (arena.getTimer() % 3600) / 60;
        int seconds = arena.getTimer() % 60;

        timerString = String.format("%02dmin%02ds", minutes, seconds);
        arena.getPlayers().forEach(pls -> Objects.requireNonNull(Bukkit.getPlayer(pls)).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(timerString)));

        if (arena.getTimer() == 590) {
            Player player = Bukkit.getPlayer(arena.getSeekers().getFirst());
            if (player == null) return;

            player.teleport(arena.getSpawnLoc());
            arena.sendMessage("&cLe loup " + player.getName() + " est libéré... Courez vite !");

            wolfFree = true;

            arenaState.setPlayingBecomeWolfArenaTask(new PlayingBecomeWolfArenaTask(arena));
            arenaState.getPlayingBecomeWolfArenaTask().runTaskTimer(arena.getGameManager().getMain(), 0, 20);
        }

        if (wolfFree && arena.getTimer() % 120 == 0) {
            arena.sendMessage(arena.getGameManager().getPrefix() + "Le joueur le plus longtemps caché actuellement est " + arena.getPlayerWithLowestTime().getName() + ".");
        }

    }

}