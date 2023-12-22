package fr.cel.cachecache.manager.items;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.hub.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class SoundItem extends GroundItem {

    private static final List<String> lores = List.of("Cet objet vous permet de vous mettre des sons aux autres joueurs.");

    private final Inventory inventory;
    private List<Sound> goatHornSounds = Arrays.asList(Sound.ITEM_GOAT_HORN_SOUND_4, Sound.ITEM_GOAT_HORN_SOUND_7);

    public SoundItem() {
        super("soundItem", Material.NOTE_BLOCK, "Sons", lores, 1);

        inventory = Bukkit.createInventory(null, 9, "Sons");

        ItemStack goat_horn = new ItemBuilder(Material.GOAT_HORN).setDisplayName("Corne de chèvres").toItemStack();
        ItemStack cat = new ItemBuilder(Material.STRING).setDisplayName("Chats").toItemStack();

        inventory.addItem(goat_horn, cat);
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        player.openInventory(inventory);
    }

}