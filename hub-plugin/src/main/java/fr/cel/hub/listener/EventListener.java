package fr.cel.hub.listener;

import fr.cel.hub.Hub;
import fr.cel.hub.tasks.FireworkMusicEvent;
import fr.cel.hub.utils.ItemBuilder;
import fr.cel.hub.utils.RPUtils;
import fr.cel.hub.utils.RPUtils.CustomMusic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EventListener extends HubListener {

    private Inventory putMusic;
    private Sound currentSound;

    private boolean eventActivated = false;
    private FireworkMusicEvent fireworkMusicEvent;

    private final World musicLocation = Bukkit.getWorld("world");
    private final Location location;

    public EventListener(Hub main) {
        super(main);
        this.location = new Location(musicLocation, 280.920, 65, 62.415);
    }

    @EventHandler
    public void interactInventory(InventoryClickEvent event) {
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!main.getPlayerManager().containsPlayerInHub(player)) return;

        Component nameInventory = event.getView().title();
        
        ItemStack item = event.getCurrentItem();

        if (item == null) return;

        Material itemMaterial = item.getType();

        if (nameInventory.equals(Component.text("Événements"))) {
            event.setCancelled(true);

            switch (itemMaterial) {
                case JUKEBOX -> {
                    if (putMusic == null) putMusic = createMusicInventory();
                    player.openInventory(putMusic);
                }

                case FIREWORK_ROCKET -> {
                    if (fireworkMusicEvent == null) {
                        fireworkMusicEvent = new FireworkMusicEvent(main);
                    }
                    if (!eventActivated) {
                        eventActivated = true;
                        fireworkMusicEvent.runTaskTimer(main, 0, 20);
                    } else {
                        eventActivated = false;
                        fireworkMusicEvent.cancel();
                    }
                    player.closeInventory();
                }

                case PLAYER_HEAD -> {
                    player.closeInventory();
                    sendMessageWithPrefix(player, "Bientôt disponible...");
                }

                default -> {}
            }

            return;
        }

        if (nameInventory.equals(Component.text("Mettre de la Musique"))) {
            event.setCancelled(true);

            if (itemMaterial == Material.BARRIER) {
                if (this.currentSound != null) {
                    Bukkit.getOnlinePlayers().forEach(pl -> pl.stopSound(this.currentSound, SoundCategory.RECORDS));
                    sendMessageWithPrefix(player, "Vous avez arrêté la musique en cours.");
                    player.closeInventory();
                } else {
                    sendMessageWithPrefix(player, "Il n'y a pas de musique actuellement.");
                }
                return;
            }

            Component nameItem = item.getItemMeta().displayName();

            if (main.getRPUtils().getMusics().get(nameItem) == null) return;

            CustomMusic customMusic = main.getRPUtils().getMusics().get(nameItem);
            Sound sound = customMusic.getSound();

            if (this.currentSound != null) {
                Bukkit.getOnlinePlayers().forEach(pl -> pl.stopSound(this.currentSound, SoundCategory.RECORDS));
                player.closeInventory();
            }

            this.currentSound = sound;
            musicLocation.playSound(location, sound, SoundCategory.RECORDS, 1.0f, 1.0f);
            sendMessageWithPrefix(player, "Vous avez mis la musique " + customMusic.getDisplayName());
            player.closeInventory();
        }

    }

    private Inventory createMusicInventory() {
        Inventory inv = Bukkit.createInventory(null, 18, Component.text("Mettre de la Musique"));

        for (CustomMusic customMusic : CustomMusic.values()) {
            ItemBuilder itemBuilder = new ItemBuilder(Material.JUKEBOX);

            itemBuilder.setDisplayName(customMusic.getMusicName().decoration(TextDecoration.ITALIC, false));

            if (customMusic.getAuthor() != null) {
                itemBuilder.addLoreLineC(customMusic.getAuthor());
            }

            if (customMusic.getDescription() != null) {
                itemBuilder.addLoreLineC(customMusic.getDescription());
            }
            
            inv.addItem(itemBuilder.toItemStack());
        }

        inv.setItem(17, new ItemBuilder(Material.BARRIER).setDisplayName(Component.text("Enlever la musique").decoration(TextDecoration.ITALIC, false)).toItemStack());

        return inv;
    }

}