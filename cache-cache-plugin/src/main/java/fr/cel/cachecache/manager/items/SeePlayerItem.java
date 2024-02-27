package fr.cel.cachecache.manager.items;

import java.util.List;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.items.inventory.SeePlayerInventory;
import fr.cel.gameapi.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.cel.cachecache.manager.GroundItem;
import org.bukkit.inventory.Inventory;

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