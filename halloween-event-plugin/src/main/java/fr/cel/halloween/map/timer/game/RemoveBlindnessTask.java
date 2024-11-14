package fr.cel.halloween.map.timer.game;

import fr.cel.halloween.map.HalloweenMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class RemoveBlindnessTask extends BukkitRunnable {

    private final HalloweenMap map;
    private int timer = 8;

    public RemoveBlindnessTask(HalloweenMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        if (timer > 0) {
            timer--;
        }

        if (timer == 0) {
            Player player = Bukkit.getPlayer(map.getTracker().getFirst());
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, false, false, false));

            for (UUID uuid : map.getSouls()) {
                Player pl = Bukkit.getPlayer(uuid);
                if (pl == null) continue;
                player.showPlayer(map.getGameManager().getMain(), pl);
            }

            cancel();
        }
    }

}