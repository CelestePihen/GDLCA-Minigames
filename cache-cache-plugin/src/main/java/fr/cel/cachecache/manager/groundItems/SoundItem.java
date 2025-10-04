package fr.cel.cachecache.manager.groundItems;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.manager.groundItems.inventory.SoundInventory;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.GameAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundItem extends GroundItem {

    private static final List<Component> LORES = List.of(
            Component.text("Cet objet vous permet de vous mettre des sons aux autres joueurs."));

    public SoundItem() {
        super("soundItem", Material.NOTE_BLOCK, Component.text("Sons"), LORES, "cc_sound");
    }

    @Override
    public void onInteract(Player player, CCMap map) {
        GameAPI.getInstance().getInventoryManager().openInventory(new SoundInventory(map), player);
    }

}