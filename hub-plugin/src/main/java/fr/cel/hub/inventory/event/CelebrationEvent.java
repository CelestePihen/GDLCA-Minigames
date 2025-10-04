package fr.cel.hub.inventory.event;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.hub.Hub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CelebrationEvent extends AbstractInventory {

    public CelebrationEvent() {
        super(Component.text("Célébration"), 9);
    }

    @Override
    protected void addItems(Inventory inventory) {
        setItem(0, new ItemBuilder(Material.JACK_O_LANTERN).itemName(Component.text("Halloween", NamedTextColor.GOLD)).addLoreLine(Hub.getInstance().getConfig().getBoolean("halloweenEvent") ? Component.text("Activé", NamedTextColor.GREEN) : Component.text("Désactivé", NamedTextColor.RED)).toItemStack());
        setItem(1, new ItemBuilder(Material.SNOW_BLOCK).itemName(Component.text("Noël", NamedTextColor.GOLD)).addLoreLine(Hub.getInstance().getConfig().getBoolean("christmasEvent") ? Component.text("Activé", NamedTextColor.GREEN) : Component.text("Désactivé", NamedTextColor.RED)).toItemStack());
        setItem(8, new ItemBuilder(Material.BARRIER).itemName(Component.text("Quitter")).toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack itemStack) {
        switch (itemStack.getType()) {
            case JACK_O_LANTERN -> {
                if (Hub.getInstance().getConfig().getBoolean("halloweenEvent")) {
                    Hub.getInstance().getConfig().set("halloweenEvent", false);
                    Hub.getInstance().saveConfig();
                    player.sendMessage(GameAPI.getPrefix() + "L'événement Halloween a été désactivé.");
                } else {
                    Hub.getInstance().getConfig().set("halloweenEvent", true);
                    Hub.getInstance().saveConfig();
                    player.sendMessage(GameAPI.getPrefix() + "L'événement Halloween a été activé.");
                }

                setItem(0, new ItemBuilder(Material.JACK_O_LANTERN).itemName(Component.text("Halloween", NamedTextColor.GOLD)).addLoreLine(Hub.getInstance().getConfig().getBoolean("halloweenEvent") ? Component.text("Activé", NamedTextColor.GREEN) : Component.text("Désactivé", NamedTextColor.RED)).toItemStack());
            }

            case SNOW_BLOCK -> player.sendMessage("Bientôt...");

            case BARRIER -> player.closeInventory();

            default -> { }
        }
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}