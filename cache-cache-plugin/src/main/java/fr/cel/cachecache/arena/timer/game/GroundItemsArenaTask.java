package fr.cel.cachecache.arena.timer.game;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.arena.CCArena;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class GroundItemsArenaTask extends BukkitRunnable {

    private final CCArena arena;

    public GroundItemsArenaTask(CCArena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        if (arena.getHunterMode() == CCArena.HunterMode.Normal) {
            spawnItem();
        } else if (arena.getHunterMode() == CCArena.HunterMode.PluieDeBonus) {
            spawnItem().setGlowing(true);
            spawnItem().setGlowing(true);
        }
    }

    private Item spawnItem() {
        Random r = new Random();
        Location randomLocation = arena.getLocationGroundItems().get(r.nextInt(arena.getLocationGroundItems().size()));
        GroundItem rGroundItem = arena.getAvailableGroundItems().get(r.nextInt(arena.getAvailableGroundItems().size()));

        ItemStack itemStack = rGroundItem.getItemStack();

        Item droppedItem = randomLocation.getWorld().dropItem(randomLocation, itemStack);
        droppedItem.setUnlimitedLifetime(true);
        arena.getSpawnedGroundItems().add(droppedItem);
        arena.sendMessage("Un objet est apparu !");

        return droppedItem;
    }
    
}