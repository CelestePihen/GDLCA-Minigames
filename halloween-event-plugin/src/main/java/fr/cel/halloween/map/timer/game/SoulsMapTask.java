package fr.cel.halloween.map.timer.game;

import fr.cel.halloween.map.HalloweenMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class SoulsMapTask extends BukkitRunnable {

    private final HalloweenMap map;

    public SoulsMapTask(HalloweenMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        spawnItem();
    }

    private void spawnItem() {
        Location randomLocation = map.getSoulLocations().get(new Random().nextInt(map.getSoulLocations().size()));

        Item droppedItem = Bukkit.getWorld("world").dropItem(randomLocation, new ItemStack(Material.NETHER_WART));
        droppedItem.setUnlimitedLifetime(true);
    }
    
}