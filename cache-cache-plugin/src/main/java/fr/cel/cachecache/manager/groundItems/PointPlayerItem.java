package fr.cel.cachecache.manager.groundItems;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.manager.groundItems.inventory.PointPlayerInventory;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.GameAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class PointPlayerItem extends GroundItem {

    private static final List<String> lores = List.of("Cet objet vous permet de pointer la direction d'un joueur.");

    public PointPlayerItem() {
        super("pointPlayerItem", Material.FEATHER, "Pointer un joueur", lores, "cc_position");
    }

    @Override
    public void onInteract(Player player, CCMap map) {
        if (map.getTimer() >= 31) {
            GameAPI.getInstance().getInventoryManager().openInventory(new PointPlayerInventory(map, player), player);
        } else {
            player.sendMessage(Component.text("Vous ne pouvez pas utiliser cette objet avant l'arrivée du chercheur."));
        }
    }
    
}