package fr.cel.hub.inventory.event;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.hub.Hub;
import fr.cel.hub.manager.dj.DJManager;
import fr.cel.hub.manager.dj.DJMode;
import fr.cel.hub.manager.dj.DJMusic;
import fr.cel.hub.manager.dj.DJPlaylist;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;

public class MusicInventory extends AbstractInventory {

    private static final ItemStack SINGLE_ITEM = new ItemBuilder(Material.MUSIC_DISC_CAT)
            .itemName(Component.text("Mode de lecture : " + DJMode.SINGLE.getDisplayName()))
            .addLoreLine(Component.text("> " + DJMode.SINGLE.getDisplayName(), NamedTextColor.GREEN))
            .addLoreLine(Component.text(DJMode.PLAYLIST_LOOP.getDisplayName()))
            .addLoreLine(Component.text(DJMode.SHUFFLE.getDisplayName()))
            .toItemStack();

    private static final ItemStack PLAYLIST_ITEM = new ItemBuilder(Material.MUSIC_DISC_5)
            .itemName(Component.text("Mode de lecture : " + DJMode.PLAYLIST_LOOP.getDisplayName()))
            .addLoreLine(Component.text(DJMode.SINGLE.getDisplayName()))
            .addLoreLine(Component.text("> " + DJMode.PLAYLIST_LOOP.getDisplayName(), NamedTextColor.GREEN))
            .addLoreLine(Component.text(DJMode.SHUFFLE.getDisplayName()))
            .toItemStack();

    private static final ItemStack SHUFFLE_ITEM = new ItemBuilder(Material.MUSIC_DISC_BLOCKS)
            .itemName(Component.text("Mode de lecture : " + DJMode.SHUFFLE.getDisplayName()))
            .addLoreLine(Component.text(DJMode.SINGLE.getDisplayName()))
            .addLoreLine(Component.text(DJMode.PLAYLIST_LOOP.getDisplayName()))
            .addLoreLine(Component.text("> " + DJMode.SHUFFLE.getDisplayName(), NamedTextColor.GREEN))
            .toItemStack();

    private static final ItemStack RELOAD_ITEM = new ItemBuilder(Material.STRUCTURE_VOID)
            .itemName(Component.text("Recharger les musiques / playlists")).toItemStack();

    private static final ItemStack STOP_ITEM = new ItemBuilder(Material.BARRIER)
            .itemName(Component.text("Stopper la musique / playlist")).toItemStack();

    private static final ItemStack NEXT_ITEM = new ItemBuilder(Material.ARROW)
            .itemName(Component.text("Passer à la musique suivante")).toItemStack();

    private final DJManager djManager = Hub.getInstance().getDjManager();

    public MusicInventory() {
        super(Component.text("Musique / DJ"), 36);
    }

    @Override
    protected void addItems(Inventory inv) {
        // Musiques individuelles
        djManager.getMusics().stream()
                .sorted(Comparator.comparing(DJMusic::musicName))
                .forEach(djMusic -> {
                    ItemBuilder item = new ItemBuilder(Material.JUKEBOX).itemName(Component.text(djMusic.musicName()));

                    if (djMusic.author() != null)
                        item.addLoreLine(Component.text("Auteur(s) : " + djMusic.author()));

                    if (djMusic.description() != null) item.addLoreLine(Component.text(djMusic.description()));

                    item.addLoreLine(Component.text("Durée : " + djMusic.durationSeconds() + "s", NamedTextColor.GRAY));

                    if (djManager.getCurrentCustomSound() != null &&
                            djManager.getCurrentCustomSound().equals(djMusic.customSound())) {
                        item.setGlow().addLoreLine(Component.text("▶ En cours", NamedTextColor.GREEN));
                    }

                    inv.addItem(item.toItemStack());
                });

        // Playlists
        int playlistStart = 27;
        for (DJPlaylist playlist : djManager.getPlaylists()) {
            ItemBuilder item = new ItemBuilder(Material.MUSIC_DISC_13)
                    .itemName(Component.text("Playlist : " + playlist.getPlaylistName()));

            playlist.getMusics().forEach(music ->
                    item.addLoreLine(Component.text(" - " + music.musicName())));

            if (djManager.getCurrentPlaylist() != null && djManager.getCurrentPlaylist().equals(playlist)) {
                item.setGlow().addLoreLine(Component.text("▶ En cours", NamedTextColor.GREEN));
            }

            inv.setItem(playlistStart++, item.toItemStack());
        }

        // Actions
        changeModeItem(inv);
        inv.setItem(getSize() - 3, RELOAD_ITEM);
        inv.setItem(getSize() - 2, STOP_ITEM);
        inv.setItem(getSize() - 1, NEXT_ITEM);
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (itemName.startsWith("Mode de lecture")) {
            DJMode mode = djManager.getCurrentMode();
            DJMode nextMode = switch (mode) {
                case SINGLE -> DJMode.PLAYLIST_LOOP;
                case PLAYLIST_LOOP -> DJMode.SHUFFLE;
                default -> DJMode.SINGLE;
            };

            djManager.setCurrentMode(nextMode);
            changeModeItem(player.getOpenInventory().getTopInventory());
            player.sendMessage(Hub.getPrefix().append(Component.text("Mode de lecture changé en " + nextMode.getDisplayName())));
            return;
        }

        if (item.getType() == Material.STRUCTURE_VOID) {
            djManager.reloadMusics();
            djManager.reloadPlaylists();
            player.closeInventory();
            GameAPI.getInstance().getInventoryManager().openInventory(new MusicInventory(), player);
            return;
        }

        if (item.getType() == Material.BARRIER) {
            djManager.stopMusic(player);
            player.closeInventory();
            GameAPI.getInstance().getInventoryManager().openInventory(new MusicInventory(), player);
            return;
        }

        if (item.getType() == Material.ARROW) {
            djManager.playNextMusic(player);
            player.closeInventory();
            GameAPI.getInstance().getInventoryManager().openInventory(new MusicInventory(), player);
            return;
        }

        // Vérifier si c'est une playlist
        if (item.getType() == Material.MUSIC_DISC_13) {
            for (DJPlaylist p : djManager.getPlaylists()) {
                if (p.getPlaylistName().equalsIgnoreCase(itemName.replaceFirst("Playlist : ", "").toLowerCase())) {
                    djManager.startPlaylist(p, djManager.getCurrentMode(), player);
                    player.closeInventory();
                    GameAPI.getInstance().getInventoryManager().openInventory(new MusicInventory(), player);
                    break;
                }
            }
        }

        // Vérifier si c'est une musique individuelle
        if (item.getType() == Material.JUKEBOX) {
            for (DJMusic m : djManager.getMusics()) {
                if (m.musicName().equalsIgnoreCase(itemName)) {
                    djManager.startMusic(m, player, false);
                    player.closeInventory();
                    GameAPI.getInstance().getInventoryManager().openInventory(new MusicInventory(), player);
                    break;
                }
            }
        }
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

    private void changeModeItem(Inventory inv) {
        DJMode currentMode = djManager.getCurrentMode();
        ItemStack modeItem;
        switch (currentMode) {
            case PLAYLIST_LOOP -> modeItem = PLAYLIST_ITEM;
            case SHUFFLE -> modeItem = SHUFFLE_ITEM;
            default -> modeItem = SINGLE_ITEM;
        }

        inv.setItem(getSize() - 4, modeItem);
    }

}