package fr.cel.cachecache.manager.items;

import java.util.Arrays;
import java.util.List;

import fr.cel.cachecache.manager.arena.CCArena;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.hub.utils.ItemBuilder;

public class ChangePositionItem extends GroundItem {

    private static List<String> lores = Arrays.asList("Cet objet vous permet de changer de position avec le joueur de votre choix.");

    public ChangePositionItem() {
        super("changePositionItem", Material.FEATHER, "Changer de position", lores, 1);
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        if (arena.getTimer() >= 31) {
            Inventory inventory = Bukkit.createInventory(null, 9, "Joueurs");
            arena.getPlayers().forEach(uuid -> {
                Player pl = Bukkit.getPlayer(uuid);
                if (pl == null) return;
                if (pl == player || pl.getGameMode() == GameMode.SPECTATOR) return;
                inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setDisplayName(pl.getDisplayName()).setSkullOwner(pl).toItemStack());
            });
            player.openInventory(inventory);
        } else {
            player.sendMessage("Vous ne pouvez pas utiliser cette objet avant l'arrivée du chercheur.");
        }
    }
    
}