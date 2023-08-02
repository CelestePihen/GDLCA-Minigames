package fr.cel.hub.inventory;

import fr.cel.hub.Hub;
import fr.cel.hub.listener.HListener;
import fr.cel.hub.manager.InventoryManager;
import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractInventory extends HListener {

    protected InventoryManager inventoryManager = Hub.getHub().getInventoryManager();

    @Getter private final Component inventoryName;
    private final int size;
    @Getter private Inventory inv;

    public AbstractInventory(String inventoryName, int size, Hub main) {
        super(main);
        this.inventoryName = Component.text(inventoryName);
        this.size = size;
    }

    public void createInventory() {
        inv = Bukkit.createInventory(null, size, inventoryName);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setDisplayName(Component.text(" ")).addAllItemFlags().toItemStack());
        }

        addItems(inv);
    }

    @EventHandler
    public void interactInventory(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!main.getPlayerManager().containsPlayerInHub(player)) return;

        Component nameInventory = event.getView().title();
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        Material type = item.getType();

        if (nameInventory.equals(inventoryName)) {
            event.setCancelled(true);
            interact(player, item.getItemMeta().displayName(), type, main);
        }
    }

    protected abstract void addItems(Inventory inv);
    protected abstract void interact(Player player, Component itemName, Material type, Hub main);

}
