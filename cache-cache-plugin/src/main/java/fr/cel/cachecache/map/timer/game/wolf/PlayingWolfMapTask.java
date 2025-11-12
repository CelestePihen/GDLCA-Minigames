package fr.cel.cachecache.map.timer.game.wolf;

import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.state.game.PlayingMapState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class PlayingWolfMapTask extends BukkitRunnable {

    private final CCMap map;
    private String timerString;
    private final PlayingMapState mapState;
    private boolean wolfFree;

    public PlayingWolfMapTask(CCMap map, PlayingMapState mapState) {
        this.map = map;
        this.mapState = mapState;
        this.wolfFree = false;
    }

    @Override
    public void run() {
        if (map.getTimer() <= 0) {
            cancel();
            map.checkWinOrEndGame();
            return;
        }

        map.setTimer(map.getTimer() - 1);

        int minutes = (map.getTimer() % 3600) / 60;
        int seconds = map.getTimer() % 60;

        timerString = String.format("%02dmin%02ds", minutes, seconds);
        map.getPlayers().forEach(pls -> Objects.requireNonNull(Bukkit.getPlayer(pls)).sendActionBar(Component.text(timerString)));

        if (map.getTimer() == 590) {
            Player player = Bukkit.getPlayer(map.getSeekers().getFirst());
            if (player == null) return;

            player.teleportAsync(map.getSpawnLoc());
            map.sendMessage(Component.text("Le loup " + player.getName() + " est libéré... Courez vite !", NamedTextColor.RED));

            wolfFree = true;

            mapState.setPlayingBecomeWolfMapTask(new PlayingBecomeWolfMapTask(map));
            mapState.getPlayingBecomeWolfMapTask().runTaskTimer(map.getGameManager().getMain(), 0, 20);
        }

        if (wolfFree && map.getTimer() % 120 == 0) {
            map.sendMessage(map.getGameManager().getPrefix().append(Component.text("Le joueur le plus longtemps caché actuellement est " + map.getPlayerWithLowestTime().getName() + ".")));
        }

    }

}