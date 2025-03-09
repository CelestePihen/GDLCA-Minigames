package fr.cel.cachecache.manager.groundItems;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.manager.groundItems.inventory.PointPlayerInventory;
import fr.cel.gameapi.GameAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class PointPlayerItem extends GroundItem {

    private static final List<String> lores = List.of("Cet objet vous permet de pointer la direction d'un joueur.");

    public PointPlayerItem() {
        super("seePlayerItem", Material.FEATHER, "Pointer un joueur", lores, "cc_position");
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        if (arena.getTimer() >= 31) {
            GameAPI.getInstance().getInventoryManager().openInventory(new PointPlayerInventory(arena), player);
        } else {
            player.sendMessage("Vous ne pouvez pas utiliser cette objet avant l'arrivée du chercheur.");
        }
    }
    
}