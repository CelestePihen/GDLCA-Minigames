package fr.cel.cachecache.manager.groundItems;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.cel.cachecache.manager.Arena;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.GroundItem;

public class SoundCatItem extends GroundItem {

    private static List<String> lores = Arrays.asList("Cet objet vous permet d'envoyer des sons de chat pendant 10 secondes.");

    public SoundCatItem() {
        super("soundCatItem", Material.STRING, "Sons de chat", lores);
    }

    @Override
    public void onInteract(Player player, Arena arena) {
        SoundCatTimer soundCatTimer = new SoundCatTimer(arena);
        soundCatTimer.runTaskTimer(GameManager.getGameManager().getMain(), 0, 20);

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() == 1) player.getInventory().setItemInMainHand(null);
        else itemInHand.setAmount(itemInHand.getAmount() - 1);
    }
    
}