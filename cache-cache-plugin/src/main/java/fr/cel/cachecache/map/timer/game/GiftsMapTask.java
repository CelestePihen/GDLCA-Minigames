package fr.cel.cachecache.map.timer.game;

import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class GiftsMapTask extends BukkitRunnable {

    private final CCMap map;

    public GiftsMapTask(CCMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        spawnGifts();
        spawnGifts();
    }

    private void spawnGifts() {
        Location randomLocation = map.getGiftLocations().get(ThreadLocalRandom.current().nextInt(map.getGiftLocations().size()));
        ItemStack gift = new ItemBuilder(Material.PAPER).setItemModel("cc_gift").itemName(Component.text("Cadeau")).toItemStack();

        Item droppedItem = randomLocation.getWorld().dropItem(randomLocation, gift);
        droppedItem.setUnlimitedLifetime(true);
        droppedItem.setGlowing(true);

        map.getSpawnedGroundItems().add(droppedItem.getUniqueId());

        map.sendMessage(Component.text("Un cadeau est apparu !"));
    }

}