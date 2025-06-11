package fr.cel.hub.inventory.event;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EventInventory extends AbstractInventory {

    public EventInventory() {
        super("Événements", 27);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(10, new ItemBuilder(Material.JUKEBOX).setItemName("Mettre de la Musique").toItemStack());
        inv.setItem(13, new ItemBuilder(Material.FIREWORK_ROCKET).setItemName("Activer le Système").toItemStack());
        inv.setItem(16, new ItemBuilder(Material.PLAYER_HEAD).setItemName("???").toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case JUKEBOX -> GameAPI.getInstance().getInventoryManager().openInventory(new MusicInventory(), player);

            case FIREWORK_ROCKET, PLAYER_HEAD ->
                // TODO à faire
                player.sendMessage(GameAPI.getPrefix() + "Bientôt disponible...");

            default -> {}
        }
    }

}