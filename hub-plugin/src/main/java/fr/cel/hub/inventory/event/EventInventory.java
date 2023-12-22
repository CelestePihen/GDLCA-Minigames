package fr.cel.hub.inventory.event;

import fr.cel.hub.Hub;
import fr.cel.hub.inventory.AbstractInventory;
import fr.cel.hub.tasks.FireworkMusicEvent;
import fr.cel.hub.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EventInventory extends AbstractInventory {

    private boolean eventActivated = false;
    private FireworkMusicEvent fireworkMusicEvent;

    public EventInventory(Hub main) {
        super("Événements", 27, main);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(10, new ItemBuilder(Material.JUKEBOX).setDisplayName("Mettre de la Musique").toItemStack());
        inv.setItem(13, new ItemBuilder(Material.FIREWORK_ROCKET).setDisplayName("Activer le Système").toItemStack());
        inv.setItem(16, new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("???").toItemStack());
    }

    @Override
    protected void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case JUKEBOX -> player.openInventory(main.getInventoryManager().getInventory("music"));

            case FIREWORK_ROCKET -> {
                if (!eventActivated) {
                    eventActivated = true;
                    fireworkMusicEvent = new FireworkMusicEvent(main);
                    fireworkMusicEvent.runTaskTimer(main, 0, 20);
                } else {
                    eventActivated = false;
                    fireworkMusicEvent.cancel();
                    fireworkMusicEvent = null;
                }
                player.closeInventory();
            }

            case PLAYER_HEAD -> {
                player.closeInventory();
                sendMessageWithPrefix(player, "Bientôt disponible...");
            }

            default -> {}
        }
    }

}