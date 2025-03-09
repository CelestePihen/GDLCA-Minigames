package fr.cel.cachecache.arena.timer.game;

import fr.cel.cachecache.arena.CCArena;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayingArenaTask extends BukkitRunnable {

    private final CCArena arena;

    public PlayingArenaTask(CCArena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        arena.setTimer(getTimer() + 1);

        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getTimerString()));
        }

        if (arena.getCcMode() == CCArena.CCMode.LoupToucheTouche && getTimer() == 5) {
            loupToucheToucheMessage();
        } else if (arena.getCcMode() != CCArena.CCMode.LoupToucheTouche) {
            if (getTimer() == 30) {
                normalMessage();
            }
            else if (getTimer() == 420) {
                arena.getCheckAdvancements().checkPasBesoin();
            }
            else if (getTimer() == 480) {
                arena.getCheckAdvancements().checkPasEssouffle();
            }
            else if (getTimer() == 1200) {
                arena.getCheckAdvancements().givePiqueNique();
            }
        }

        if (arena.getHiders().size() == 1 && arena.getNbPlayerBeginning() > 2) {
            arena.getCheckAdvancements().giveToujoursVivant();
        }

        // Succès
        arena.getCheckAdvancements().checkAimezFaireMal();
        arena.getCheckAdvancements().checkCollectionPersonnelleAndRatLabo();
        arena.getCheckAdvancements().checkPiedPouvoir();
    }

    private void loupToucheToucheMessage() {
        Player player = Bukkit.getPlayer(arena.getSeekers().getFirst());
        player.teleport(arena.getSpawnLoc());
        arena.sendMessage("&cLe loup " + player.getName() + " est libéré(e)... Courez vite avant qu'il ne vous attrape !");
    }

    private void normalMessage() {
        List<String> names = new ArrayList<>();
        arena.getSeekers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.teleport(arena.getSpawnLoc());
            names.add(player.getName());
        });
        if (names.size() == 1) {
            arena.sendMessage("&cLe chercheur " + names.getFirst() + " est libéré... Cachez-vous !");
        } else {
            arena.sendMessage("&cLes chercheurs " + names.getFirst() + " et " + names.get(1) + " sont libérés... Cachez-vous !");
        }
    }

    private String getTimerString() {
        int hours = getTimer() / 3600;
        int minutes = (getTimer() % 3600) / 60;
        int seconds = getTimer() % 60;

        String timerString;

        if (minutes == 0 && hours == 0) {
            if (seconds < 10) {
                if (seconds == 0 || seconds == 1) {
                    timerString = String.format("%01d seconde", seconds);
                } else {
                    timerString = String.format("%01d secondes", seconds);
                }
            } else {
                timerString = String.format("%02d secondes", seconds);
            }
        }
        else if (hours == 0) {
            timerString = String.format("%02dmin%02ds", minutes, seconds);
        }
        else {
            timerString = String.format("%02dh%02dmin%02ds", hours, minutes, seconds);
        }

        return timerString;
    }

    private int getTimer() {
        return arena.getTimer();
    }
    
}