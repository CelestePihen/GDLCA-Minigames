package fr.cel.hub.manager.dj;

import fr.cel.gameapi.GameAPI;
import fr.cel.hub.Hub;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DJBossBar {

    private final DJMusic music;
    private final BossBar bossBar;
    private final Location center;
    private final double radiusSquared;

    private final Set<UUID> playersInRange = new HashSet<>();

    private int elapsed = 0;
    private BukkitRunnable runnable;

    public DJBossBar(DJMusic music, Location center, double radius) {
        this.music = music;
        this.bossBar = BossBar.bossBar(
                Component.text("🎵 " + music.musicName() + " [00:00 / " + formatTime(music.durationSeconds()) + "]"),
                0f,
                BossBar.Color.GREEN,
                BossBar.Overlay.NOTCHED_10
        );
        this.center = center;
        this.radiusSquared = radius * radius;
    }

    public void removeHubPlayers() {
        if (runnable != null) runnable.cancel();

        for (UUID uuid : GameAPI.getInstance().getPlayerManager().getPlayersInHub()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) player.hideBossBar(bossBar);
        }
    }

    public void start() {
        for (UUID uuid : GameAPI.getInstance().getPlayerManager().getPlayersInHub()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;

            player.sendActionBar(Component.text("🎵 En cours : " + music.musicName() + " par " + music.author()));
        }

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (elapsed > music.durationSeconds()) {
                    removeHubPlayers();
                    cancel();
                    return;
                }

                for (UUID uuid : GameAPI.getInstance().getPlayerManager().getPlayersInHub()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !player.isOnline()) continue;

                    boolean inRange = player.getWorld().equals(center.getWorld()) && player.getLocation().distanceSquared(center) <= radiusSquared;

                    if (inRange && !playersInRange.contains(player.getUniqueId())) {
                        player.showBossBar(bossBar);
                        playersInRange.add(player.getUniqueId());
                    }

                    else if (!inRange && playersInRange.contains(player.getUniqueId())) {
                        player.hideBossBar(bossBar);
                        playersInRange.remove(player.getUniqueId());
                    }
                }

                bossBar.name(Component.text("🎵 " + music.musicName() + " [" + formatTime(elapsed) + " / " + formatTime(music.durationSeconds()) + "]"));
                bossBar.progress((float) elapsed / music.durationSeconds());
                elapsed++;
            }
        };
        runnable.runTaskTimer(Hub.getInstance(), 0L, 20L);
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

}