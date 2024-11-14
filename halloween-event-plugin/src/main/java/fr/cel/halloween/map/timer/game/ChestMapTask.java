package fr.cel.halloween.map.timer.game;

import fr.cel.halloween.map.HalloweenMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChestMapTask extends BukkitRunnable {

    private final HalloweenMap map;

    public ChestMapTask(HalloweenMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        enableRandomChest();
    }

    private void enableRandomChest() {
        List<Location> inactiveChests = map.getChestLocations();
        inactiveChests.removeAll(map.getActiveChests());

        if (inactiveChests.isEmpty()) return;

        Collections.shuffle(inactiveChests);
        Location chestLocation = inactiveChests.get(new Random().nextInt(inactiveChests.size()));

        if (chestLocation.getBlock().getType() == Material.CHEST) {
            map.getActiveChests().add(chestLocation);
        }
    }

}