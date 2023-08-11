package fr.cel.cachecache.manager.arena.timer.game;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.GroundItem;

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
        String randomLocation = arena.getLocationGroundItems().get(r.nextInt(arena.getLocationGroundItems().size()));
        GroundItem rGroundItem = arena.getAvailableGroundItems().get(r.nextInt(arena.getAvailableGroundItems().size()));

        Location location = parseStringToLoc(randomLocation);
        ItemStack itemStack = rGroundItem.getItemStack();

        Item droppedItem = location.getWorld().dropItem(location, itemStack);
        droppedItem.setUnlimitedLifetime(true);
        arena.getSpawnedGroundItems().add(droppedItem);
        arena.sendMessage("Un objet est apparu !");

        return droppedItem;
    }

    private Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");

        double x = Double.parseDouble(parsedLoc[0]);
        double y = Double.parseDouble(parsedLoc[1]);
        double z = Double.parseDouble(parsedLoc[2]);

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }
    
}