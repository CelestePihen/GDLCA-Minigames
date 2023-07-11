package fr.cel.cachecache.manager.arena.timer.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.cachecache.manager.Arena;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayingArenaTask extends BukkitRunnable {

    private final Arena arena;
    private String timerString;

    public PlayingArenaTask(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        arena.setTimer(getTimer() + 1);

        int hours = getTimer() / 3600;
        int minutes = (getTimer() % 3600) / 60;
        int seconds = getTimer() % 60;

        timerString = String.format("%02dh%02dmin%02ds", hours, minutes, seconds);
        arena.getPlayers().forEach(pls -> {
            Bukkit.getPlayer(pls).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(timerString));
        });

        if (getTimer() == 30) {
            List<String> names = new ArrayList<>();
            arena.getSeekers().forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                player.teleport(arena.getSpawnLoc());
                names.add(player.getName());
            });
            if (names.size() == 1) {
                arena.sendMessage("&cLe chercheur " + names.get(0) + " est libéré... Cachez-vous !");
            } else {
                arena.sendMessage("&cLes chercheurs " + names + " sont libérés... Cachez-vous !");
            }
            
        }

        if (getTimer() == 600) {
            arena.getSeekers().forEach(pls -> { Bukkit.getPlayer(pls).setGlowing(false); });
            arena.sendMessage("&cLes chercheurs n'ont plus de surbrillance.");
        }

    }

    private int getTimer() {
        return arena.getTimer();
    }
    
}