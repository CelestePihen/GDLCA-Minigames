package fr.cel.halloween.map.timer.game;

import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.state.game.PlayingMapState;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitingMapTask extends BukkitRunnable {

    private final HalloweenMap map;
    private int timer = 2;

    public WaitingMapTask(HalloweenMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        if (timer <= 0) {
            cancel();
            map.setMapState(new PlayingMapState(map));
            return;
        }

        timer--;
        map.setLevel(timer);
        
    }
    
}