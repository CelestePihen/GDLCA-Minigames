package fr.cel.cachecache.map.timer.pregame;

import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.state.game.WaitingMapState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingMapTask extends BukkitRunnable {

    private final CCMap map;
    private int timer;

    public StartingMapTask(CCMap map, int timer) {
        this.map = map;
        this.timer = timer;
    }

    @Override
    public void run() {
        if (timer <= 0) {
            map.setMapState(new WaitingMapState(map));
            return;
        }

        timer--;
        map.setLevel(timer);

        if (timer == 10 || (timer <= 5 && timer != 0)) {
            map.sendMessage("Démarre dans " + timer + getTextSeconds() + "...");
            map.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }
    }
    
    private String getTextSeconds() {
        return timer == 1 ? " seconde" : " secondes";
    }

}