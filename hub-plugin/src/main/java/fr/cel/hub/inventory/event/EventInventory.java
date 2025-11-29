package fr.cel.hub.inventory.event;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.hub.Hub;
import fr.cel.hub.manager.dj.DJManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EventInventory extends AbstractInventory {

    private final DJManager djManager = Hub.getInstance().getDjManager();

    public EventInventory() {
        super(Component.text("Événements"), 27);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(10, new ItemBuilder(Material.JUKEBOX).itemName(Component.text("Mettre de la Musique")).toItemStack());
        inv.setItem(13, new ItemBuilder(Material.FIREWORK_ROCKET).itemName(Component.text("Activer le Système")).toItemStack());
        inv.setItem(16, new ItemBuilder(Material.PLAYER_HEAD).itemName(Component.text("???")).toItemStack());
    }

    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, ItemStack item) {
        switch (item.getType()) {
            case JUKEBOX -> GameAPI.getInstance().getInventoryManager().openInventory(new MusicInventory(), player);

            case FIREWORK_ROCKET -> djManager.activateSystem(player);

            case PLAYER_HEAD ->
                // TODO à faire
                player.sendMessage(Hub.getPrefix().append(Component.text("Bientôt disponible...")));

            default -> {}
        }
    }

}