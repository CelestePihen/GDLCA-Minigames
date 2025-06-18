package fr.cel.cachecache.arena.timer.game;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.manager.GroundItem;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class GroundItemsArenaTask extends BukkitRunnable {

    private final CCArena arena;

    public GroundItemsArenaTask(CCArena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        if (arena.getCcMode() == CCArena.CCMode.PluieDeBonus) {
            spawnItem().setGlowing(true);
            spawnItem().setGlowing(true);
        } else {
            spawnItem();
        }
    }

    private Item spawnItem() {
        Location randomLocation = arena.getLocationGroundItems().get(ThreadLocalRandom.current().nextInt(arena.getLocationGroundItems().size()));
        GroundItem rGroundItem = arena.getAvailableGroundItems().get(ThreadLocalRandom.current().nextInt(arena.getLocationGroundItems().size()));

        Item droppedItem = randomLocation.getWorld().dropItem(randomLocation, rGroundItem.getItemStack());
        droppedItem.setUnlimitedLifetime(true);
        arena.getSpawnedGroundItems().add(droppedItem);
        arena.sendMessage("Un objet est apparu !");

        return droppedItem;
    }
    
}