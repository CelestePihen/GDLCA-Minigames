package fr.cel.hub.inventory.event;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.gameapi.utils.RPUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MusicInventory extends AbstractInventory {

    private Sound currentSound;

    private final World musicLocation = Bukkit.getWorld("world");
    private final Location location;

    public MusicInventory() {
        super("Mettre de la Musique", 27);
        this.currentSound = null;
        this.location = new Location(musicLocation, 270.5, 64, 59.5);
    }

    @Override
    protected void addItems(Inventory inv) {
        GameAPI.getInstance().getRpUtils().getMusics().forEach((name, customMusic) -> {
            ItemBuilder itemBuilder = new ItemBuilder(Material.JUKEBOX);

            itemBuilder.setDisplayName(customMusic.getMusicName());

            if (customMusic.getAuthor() != null) {
                itemBuilder.addLoreLine(customMusic.getAuthor());
            }

            if (customMusic.getDescription() != null) {
                itemBuilder.addLoreLine(customMusic.getDescription());
            }

            inv.addItem(itemBuilder.toItemStack());
        });

        inv.setItem(getSize() - 1, new ItemBuilder(Material.BARRIER).setDisplayName("Enlever la musique").toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (item.getType() == Material.BARRIER) {
            if (this.currentSound != null) {
                Bukkit.getOnlinePlayers().forEach(pl -> pl.stopSound(this.currentSound, SoundCategory.RECORDS));
                player.sendMessage(GameAPI.getPrefix() + "Vous avez arrêté la musique en cours.");
                player.sendMessage();
                player.closeInventory();
            } else {
                player.sendMessage(GameAPI.getPrefix() + "Il n'y a pas de musique actuellement.");
            }
            return;
        }

        RPUtils.CustomMusic customMusic = GameAPI.getInstance().getRpUtils().getMusics().get(itemName);
        if (customMusic == null) return;
        Sound sound = customMusic.getSound();

        if (this.currentSound != null) {
            Bukkit.getOnlinePlayers().forEach(pl -> pl.stopSound(this.currentSound, SoundCategory.RECORDS));
        }

        this.currentSound = sound;
        musicLocation.playSound(location, this.currentSound, SoundCategory.RECORDS, 2.0f, 1.0f);
        player.sendMessage(GameAPI.getPrefix() + "Vous avez mis la musique " + customMusic.getMusicName());
        player.closeInventory();
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}