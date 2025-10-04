package fr.cel.halloween.map.timer.game;

import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.halloween.map.HalloweenMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class SoulsMapTask extends BukkitRunnable {

    private static final Random RANDOM = new Random();

    private final HalloweenMap map;

    public SoulsMapTask(HalloweenMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        spawnItem();
    }

    private void spawnItem() {
        Location randomLocation = map.getSoulLocations().get(RANDOM.nextInt(map.getSoulLocations().size()));

        Item droppedItem = Bukkit.getWorlds().getFirst().dropItem(randomLocation,
                new ItemBuilder(Material.NETHER_WART).itemName(Component.text("Âme")).toItemStack());
        droppedItem.setUnlimitedLifetime(true);
        map.getSoulItems().add(droppedItem.getUniqueId());
    }
    
}