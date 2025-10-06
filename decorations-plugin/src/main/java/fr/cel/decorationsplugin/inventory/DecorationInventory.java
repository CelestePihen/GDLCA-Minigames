package fr.cel.decorationsplugin.inventory;

import fr.cel.decorationsplugin.manager.Decoration;
import fr.cel.decorationsplugin.manager.DecorationsManager;
import fr.cel.gameapi.inventory.AbstractInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DecorationInventory extends AbstractInventory {

    private final DecorationsManager manager;

    public DecorationInventory(DecorationsManager manager) {
        super(Component.text("Decorations"), 54);
        this.manager = manager;
    }

    @Override
    protected void addItems(Inventory inventory) {
        int slot = 0;

        List<Decoration> decorations = new ArrayList<>(manager.getDecorations().values());
        decorations.sort(Comparator.comparing(decoration -> decoration.displayName().toLowerCase()));

        for (Decoration decoration : decorations) {
            if (slot < inventory.getSize()) inventory.setItem(slot++, decoration.getDisplayItem());
        }
    }

    @Override
    public void interact(Player player, String name, ItemStack itemStack) {
        player.getInventory().addItem(itemStack);
        player.sendMessage(Component.text("Vous avez reçu la décoration : ", NamedTextColor.GREEN).append(Component.text(name, NamedTextColor.YELLOW)));
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}