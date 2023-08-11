package fr.cel.cachecache.manager.groundItems;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fr.cel.cachecache.manager.arena.CCArena;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.cel.cachecache.manager.GroundItem;

public class CursedHornItem extends GroundItem {

    private List<Sound> goatHornSounds = Arrays.asList(Sound.ITEM_GOAT_HORN_SOUND_4, Sound.ITEM_GOAT_HORN_SOUND_7);
    private static List<String> lores = Arrays.asList("Cet objet permet de faire un bruit de corne de chèvre.");

    public CursedHornItem() {
        super("cursedHornItem", Material.DRIED_KELP, "Corne de chèvres", lores, 1);
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        Sound sound = goatHornSounds.get(new Random().nextInt(goatHornSounds.size()));
        arena.getPlayers().forEach(uuid -> {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl.getGameMode() == GameMode.SPECTATOR) return;
            pl.playSound(pl, sound, SoundCategory.AMBIENT, 2.0f, 1.0f);
            
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getAmount() == 1) player.getInventory().setItemInMainHand(null);
            else itemInHand.setAmount(itemInHand.getAmount() - 1);
        });
    }
    
}