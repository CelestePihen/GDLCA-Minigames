package fr.cel.hub.inventory.event;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.hub.Hub;
import fr.cel.hub.manager.dj.DJMusic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        Hub.getInstance().getDjManager().getEntrySet().stream()
                .sorted(Map.Entry.comparingByKey(String::compareToIgnoreCase)).forEach(entry -> {
                    String name = entry.getKey();
                    DJMusic djMusic = entry.getValue();
                    ItemBuilder itemBuilder = new ItemBuilder(Material.JUKEBOX);

                    itemBuilder.itemName(Component.text(name));

                    if (djMusic.author() != null) {
                        itemBuilder.addLoreLine(Component.text(djMusic.author()));
                    }

                    if (djMusic.description() != null) {
                        itemBuilder.addLoreLine(Component.text(djMusic.description()));
                    }

                    inv.addItem(itemBuilder.toItemStack());
                });

        inv.setItem(getSize() - 2, new ItemBuilder(Material.STRUCTURE_VOID).itemName(Component.text("Recharger les musiques")).toItemStack());
        inv.setItem(getSize() - 1, new ItemBuilder(Material.BARRIER).itemName(Component.text("Stopper la musique")).toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (item.getType() == Material.STRUCTURE_VOID) {
            player.closeInventory();
            Hub.getInstance().getDjManager().reloadMusics();
            GameAPI.getInstance().getInventoryManager().openInventory(new MusicInventory(), player);
            return;
        }

        if (item.getType() == Material.BARRIER) {
            Hub.getInstance().getDjManager().stopMusic(player);
            return;
        }

        DJMusic djMusic = Hub.getInstance().getDjManager().getDJMusic(itemName);
        if (djMusic == null) {
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Erreur : Musique inconnue", NamedTextColor.RED)));
            return;
        }

        Hub.getInstance().getDjManager().startMusic(djMusic.customSound(), player);
        player.sendMessage(GameAPI.getPrefix().append(Component.text("Vous avez mis la musique " + djMusic.musicName())));
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}