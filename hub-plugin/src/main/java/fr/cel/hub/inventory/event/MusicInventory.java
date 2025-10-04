package fr.cel.hub.inventory.event;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.gameapi.utils.RPUtils;
import fr.cel.gameapi.utils.RPUtils.CustomMusic;
import fr.cel.hub.manager.MusicManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MusicInventory extends AbstractInventory {

    public MusicInventory() {
        super(Component.text("Musique"), 27);
    }

    @Override
    protected void addItems(Inventory inv) {
        RPUtils.getMusics().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String::compareToIgnoreCase)).forEach(entry -> {
                    String name = entry.getKey();
                    CustomMusic customMusic = entry.getValue();
                    ItemBuilder itemBuilder = new ItemBuilder(Material.JUKEBOX);

                    itemBuilder.itemName(Component.text(name));

                    if (customMusic.getAuthor() != null) {
                        itemBuilder.addLoreLine(Component.text(customMusic.getAuthor()));
                    }

                    if (customMusic.getDescription() != null) {
                        itemBuilder.addLoreLine(Component.text(customMusic.getDescription()));
                    }

                    inv.addItem(itemBuilder.toItemStack());
                });

        inv.setItem(getSize() - 1, new ItemBuilder(Material.BARRIER).itemName(Component.text("Enlever la musique")).toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (item.getType() == Material.BARRIER) {
            MusicManager.stopMusic(player);
            return;
        }

        CustomMusic customMusic = RPUtils.getMusics().get(itemName);
        if (customMusic == null) return;

        MusicManager.startMusic(customMusic.getCustomSound(), player);

        player.sendMessage(GameAPI.getPrefix() + "Vous avez mis la musique " + customMusic.getMusicName());
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}