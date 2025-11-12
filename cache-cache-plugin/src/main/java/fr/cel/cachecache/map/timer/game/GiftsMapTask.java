package fr.cel.cachecache.map.timer.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.state.game.PlayingMapState;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class GiftsMapTask extends BukkitRunnable {

    private final CCMap map;

    private static final int MIN_SECONDS_GIFTS_TASK = 180; // 3 minutes
    private static final int MAX_SECONDS_GIFTS_TASK = 300; // 5 minutes

    public GiftsMapTask(CCMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        cancel();

        spawnGifts();
        spawnGifts();

        GiftsMapTask newTask = new GiftsMapTask(map);
        newTask.runTaskLater(CacheCache.getInstance(), ThreadLocalRandom.current().nextInt(MIN_SECONDS_GIFTS_TASK, MAX_SECONDS_GIFTS_TASK + 1) * 20L);
        if (map.getMapState() instanceof PlayingMapState state) state.setGiftsMapTask(newTask);
    }

    private void spawnGifts() {
        Location randomLocation = map.getWinterUtility().getGiftLocations().get(ThreadLocalRandom.current().nextInt(map.getWinterUtility().getGiftLocations().size()));
        ItemStack gift = new ItemBuilder(Material.PAPER).setItemModel("cc_gift").itemName(Component.text("Cadeau")).toItemStack();

        Item droppedItem = randomLocation.getWorld().dropItem(randomLocation, gift);
        droppedItem.setUnlimitedLifetime(true);
        droppedItem.setGlowing(true);

        map.getSpawnedGroundItems().add(droppedItem.getUniqueId());

        map.sendMessage(Component.text("Deux cadeaux sont apparus !", NamedTextColor.GREEN));
    }

}