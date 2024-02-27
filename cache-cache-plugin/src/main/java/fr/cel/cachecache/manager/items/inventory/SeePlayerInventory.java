package fr.cel.cachecache.manager.items.inventory;

import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SeePlayerInventory extends AbstractInventory {

    private final CCArena arena;

    public SeePlayerInventory(CCArena arena) {
        super("Joueurs", 18);
        this.arena = arena;
    }

    @Override
    protected void addItems(Inventory inventory) {
        arena.getPlayers().forEach(uuid -> {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl == null) return;
            if (pl.getGameMode() == GameMode.SPECTATOR) return;
            inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setDisplayName(pl.getDisplayName()).setSkullOwner(pl).toItemStack());
        });
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (!arena.isPlayerInArena(player)) return;
        if (item.getType() == Material.AIR) return;

        Player target = Bukkit.getPlayer(itemName);
        if (arena.getPlayers().contains(target.getUniqueId())) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 8*20, 1, false, false, false));
            removeItem(player);
        } else {
            player.sendMessage(arena.getGameManager().getPrefix() + "Ce joueur n'est pas disponible. Merci de réouvrir le menu.");
        }

    }

    private void removeItem(Player player) {
        player.closeInventory();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() == 1) player.getInventory().setItemInMainHand(null);
        else itemInHand.setAmount(itemInHand.getAmount() - 1);
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }
}