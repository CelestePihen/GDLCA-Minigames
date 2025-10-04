package fr.cel.halloween.map.timer.pregame;

import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.state.game.WaitingMapState;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingMapTask extends BukkitRunnable {

    private final HalloweenMap map;
    private int timer;

    public StartingMapTask(HalloweenMap map, int timer) {
        this.map = map;
        this.timer = timer;
    }

    @Override
    public void run() {
        if (timer <= 0) {
            cancel();
            map.setMapState(new WaitingMapState(map));
            return;
        }

        timer--;
        map.setLevel(timer);

        if (timer == 10 || (timer <= 5 && timer != 0)) {
            map.sendMessage(Component.text("Démarre dans " + timer + getSeconds() + "..."));
            map.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }

    }
    
    private String getSeconds() {
        return timer == 1 ? " seconde" : " secondes";
    }

}