package fr.cel.cachecache.map.timer.game;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.map.CCMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class GroundItemsMapTask extends BukkitRunnable {

    private final CCMap map;

    public GroundItemsMapTask(CCMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        if (map.getCcMode() == CCMap.CCMode.PluieDeBonus) {
            spawnItem().setGlowing(true);
            spawnItem().setGlowing(true);
        } else {
            spawnItem();
        }
    }

    private Item spawnItem() {
        Location randomLocation = map.getLocationGroundItems().get(ThreadLocalRandom.current().nextInt(map.getLocationGroundItems().size()));
        GroundItem rGroundItem = map.getAvailableGroundItems().get(ThreadLocalRandom.current().nextInt(map.getLocationGroundItems().size()));

        Item droppedItem = randomLocation.getWorld().dropItem(randomLocation, rGroundItem.getItemStack());
        droppedItem.setUnlimitedLifetime(true);
        map.getSpawnedGroundItems().add(droppedItem);
        map.sendMessage(Component.text("Un objet est apparu !"));

        return droppedItem;
    }
    
}