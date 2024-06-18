package fr.cel.cachecache.manager.groundItems;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.manager.groundItems.inventory.SeePlayerInventory;
import fr.cel.gameapi.GameAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class SeePlayerItem extends GroundItem {

    private static final List<String> lores = List.of("Cet objet vous permet de connaitre la position des joueurs.");

    public SeePlayerItem() {
        super("seePlayerItem", Material.FEATHER, "Voir un joueur", lores, 1);
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        if (arena.getTimer() >= 31) {
            GameAPI.getInstance().getInventoryManager().openInventory(new SeePlayerInventory(arena), player);
        } else {
            player.sendMessage("Vous ne pouvez pas utiliser cette objet avant l'arrivée du chercheur.");
        }
    }
    
}