package fr.cel.gameapi.inventory.statistics;

import fr.cel.gameapi.inventory.AbstractInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ValoStatsInventory extends AbstractInventory {

    private final Player player;

    public ValoStatsInventory(Player player) {
        super("Statistiques - Valocraft", 27);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {

    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {

    }

}