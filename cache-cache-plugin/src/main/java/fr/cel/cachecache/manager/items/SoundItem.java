package fr.cel.cachecache.manager.items;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.items.inventory.SoundInventory;
import fr.cel.gameapi.GameAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundItem extends GroundItem {

    private static final List<String> lores = List.of("Cet objet vous permet de vous mettre des sons aux autres joueurs.");

    public SoundItem() {
        super("soundItem", Material.NOTE_BLOCK, "Sons", lores, 1);
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        GameAPI.getInstance().getInventoryManager().openInventory(new SoundInventory(arena), player);
    }

}