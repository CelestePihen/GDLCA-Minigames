package fr.cel.hub.inventory;

import fr.cel.hub.Hub;
import fr.cel.hub.listener.HListener;
import fr.cel.hub.manager.InventoryManager;
import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractInventory extends HListener {

    protected InventoryManager inventoryManager = Hub.getHub().getInventoryManager();

    @Getter private final String inventoryName;
    @Getter private final int size;
    @Getter public Inventory inv;

    public AbstractInventory(String inventoryName, int size, Hub main) {
        super(main);
        this.inventoryName = inventoryName;
        this.size = size;
    }

    public void createInventory() {
        inv = Bukkit.createInventory(null, size, inventoryName);

        if (makeGlassPane()) {
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setDisplayName(" ").addAllItemFlags().toItemStack());
            }
        }

        addItems(inv);
    }

    @EventHandler
    public void interactInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!main.getPlayerManager().containsPlayerInHub(player)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        if (event.getView().getTitle().equalsIgnoreCase(inventoryName)) {
            event.setCancelled(true);
            interact(player, item.getItemMeta().getDisplayName(), item);
        }
    }

    protected boolean makeGlassPane() {
        return true;
    }

    protected abstract void addItems(Inventory inv);
    protected abstract void interact(Player player, String itemName, ItemStack item);

}
