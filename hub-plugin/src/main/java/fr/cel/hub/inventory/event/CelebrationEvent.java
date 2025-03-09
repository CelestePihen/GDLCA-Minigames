package fr.cel.hub.inventory.event;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.hub.Hub;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CelebrationEvent extends AbstractInventory {

    public CelebrationEvent() {
        super("Célébration", 9);
    }

    @Override
    protected void addItems(Inventory inventory) {
        setItem(0, new ItemBuilder(Material.JACK_O_LANTERN).setDisplayName("&6Halloween").addLoreLine(Hub.getInstance().getConfig().getBoolean("halloweenEvent") ? ChatUtility.format("&aActivé") : ChatUtility.format("&cDésactivé")).toItemStack());
        setItem(1, new ItemBuilder(Material.SNOW_BLOCK).setDisplayName("&6Noël").addLoreLine(Hub.getInstance().getConfig().getBoolean("christmasEvent") ? ChatUtility.format("&aActivé") : ChatUtility.format("&cDésactivé")).toItemStack());
        setItem(8, new ItemBuilder(Material.BARRIER).setDisplayName("Quitter").toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack itemStack) {
        switch (itemStack.getType()) {
            case JACK_O_LANTERN -> {
                if (Hub.getInstance().getConfig().getBoolean("halloweenEvent")) {
                    Hub.getInstance().getConfig().set("halloweenEvent", false);
                    Hub.getInstance().saveConfig();
                    player.sendMessage(GameAPI.getPrefix() + "L'événement Halloween a été désactivé.");
                }

                else {
                    Hub.getInstance().getConfig().set("halloweenEvent", true);
                    Hub.getInstance().saveConfig();
                    player.sendMessage(GameAPI.getPrefix() + "L'événement Halloween a été activé.");
                }

                setItem(0, new ItemBuilder(Material.JACK_O_LANTERN).setDisplayName("&6Halloween").addLoreLine(Hub.getInstance().getConfig().getBoolean("halloweenEvent") ? ChatUtility.format("&aActivé") : ChatUtility.format("&cDésactivé")).toItemStack());
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