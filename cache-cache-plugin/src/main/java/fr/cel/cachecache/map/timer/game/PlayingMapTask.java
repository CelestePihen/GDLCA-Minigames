package fr.cel.cachecache.map.timer.game;

import fr.cel.cachecache.map.CCMap;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayingMapTask extends BukkitRunnable {

    private final CCMap map;

    public PlayingMapTask(CCMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        map.setTimer(getTimer() + 1);

        for (UUID uuid : map.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getTimerString()));
        }

        if (map.getCcMode() == CCMap.CCMode.LoupToucheTouche && getTimer() == 5) {
            loupToucheToucheMessage();
        } else if (map.getCcMode() != CCMap.CCMode.LoupToucheTouche) {
            if (getTimer() == 30) {
                normalMessage();
            }
            else if (getTimer() == 420) {
                map.getCheckAdvancements().checkPasBesoin();
            }
            else if (getTimer() == 1200) {
                map.getCheckAdvancements().givePiqueNique();
            }
        }

        if (map.getHiders().size() == 1 && map.getNbPlayerBeginning() > 2) {
            map.getCheckAdvancements().giveToujoursVivant();
        }

        // Succès
        map.getCheckAdvancements().checkAimezFaireMal();
        map.getCheckAdvancements().checkCollectionPersonnelleAndRatLabo();
        map.getCheckAdvancements().checkPiedPouvoir();
    }

    private void loupToucheToucheMessage() {
        Player player = Bukkit.getPlayer(map.getSeekers().getFirst());
        player.teleport(map.getSpawnLoc());
        map.sendMessage("&cLe loup " + player.getName() + " est libéré(e)... Courez vite avant qu'il ne vous attrape !");
    }

    private void normalMessage() {
        List<String> names = new ArrayList<>();
        map.getSeekers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.teleport(map.getSpawnLoc());
            names.add(player.getName());
        });
        if (names.size() == 1) {
            map.sendMessage("&cLe chercheur " + names.getFirst() + " est libéré... Cachez-vous !");
        } else {
            map.sendMessage("&cLes chercheurs " + names.getFirst() + " et " + names.get(1) + " sont libérés... Cachez-vous !");
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
        return map.getTimer();
    }
    
}