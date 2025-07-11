package fr.cel.cachecache.manager.groundItems.inventory;

import fr.cel.cachecache.manager.groundItems.tasks.PointPlayerTask;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.database.StatisticsManager;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PointPlayerInventory extends AbstractInventory {

    private final CCMap map;
    private final Player player;

    public PointPlayerInventory(CCMap map, Player player) {
        super("Joueurs", 18);
        this.map = map;
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inventory) {
        for (UUID uuid : map.getPlayers()) {
            Player pl = Bukkit.getPlayer(uuid);

            if (pl == null) continue;
            if (pl.getName().equals(player.getName())) continue;
            if (pl.getGameMode() == GameMode.SPECTATOR) continue;

            inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setItemName(pl.getDisplayName()).setSkullOwner(Bukkit.createProfile(pl.getUniqueId())).toItemStack());
        }
    }

    /**
     * @author Deadsky
     */
    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (!map.isPlayerInMap(player)) return;
        if (item.getType() == Material.AIR) return;

        Player target = Bukkit.getPlayer(itemName);
        if (target != null && map.getPlayers().contains(target.getUniqueId())) {
            PointPlayerTask pointPlayerTask = new PointPlayerTask(player, target);
            pointPlayerTask.runTaskTimer(map.getGameManager().getMain(), 0, 1);
            map.addItemTask(pointPlayerTask);

            GameAPI.getInstance().getStatisticsManager().updatePlayerStatistic(player, StatisticsManager.PlayerStatistics.CC_POINT_PLAYER_USAGE, 1);
            removeItem(player);
        } else {
            player.sendMessage(map.getGameManager().getPrefix() + "Ce joueur n'est plus dans la carte ou s'est déconnecté(e). Merci de réouvrir le menu.");
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