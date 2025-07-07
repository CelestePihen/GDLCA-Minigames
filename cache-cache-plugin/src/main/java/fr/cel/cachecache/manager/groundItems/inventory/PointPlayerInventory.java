package fr.cel.cachecache.manager.groundItems.inventory;

import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.database.StatisticsManager;
import fr.cel.gameapi.utils.ItemBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PointPlayerInventory extends AbstractInventory {

    private final CCMap map;

    public PointPlayerInventory(CCMap map) {
        super("Joueurs", 18);
        this.map = map;
    }

    @Override
    protected void addItems(Inventory inventory) {
        for (UUID uuid : map.getPlayers()) {
            Player pl = Bukkit.getPlayer(uuid);

            if (pl == null) continue;
            if (pl.getGameMode() == GameMode.SPECTATOR) continue;

            inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setDisplayName(pl.getDisplayName()).setSkullOwner(Bukkit.createProfile(pl.getUniqueId())).toItemStack());
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
            new BukkitRunnable() {
                int timer = 200;
                @Override
                public void run() {
                    if (timer > 0){
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getArrowDirection(player, target.getLocation())));
                        timer--;
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(map.getGameManager().getMain(), 0, 1);

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

    /**
     * Retourne une flèche vers la direction du joueur
     * @param player L'instance du joueur
     * @param targetLoc La position du joueur visé
     * @return Retourne la flèche vers la direction
     * @author Deadsky
     */
    private String getArrowDirection(Player player, Location targetLoc) {
        String arrow = "?";

        Vector a = targetLoc.toVector().setY(0).subtract(player.getLocation().toVector().setY(0));
        Vector b = player.getLocation().getDirection().setY(0);

        double angleDir = (Math.atan2(a.getZ(), a.getX()) / 2 / Math.PI * 360 + 360) % 360, angleLook = (Math.atan2(b.getZ(), b.getX()) / 2 / Math.PI * 360 + 360) % 360, angle = (angleDir - angleLook + 360) % 360;

        if (angle <= 022.5 || angle > 337.5) arrow = "⬆";
        else if (angle <= 337.5 && angle > 292.5) arrow = "⬆";
        else if (angle <= 292.5 && angle > 247.5) arrow = "←";
        else if (angle <= 347.5 && angle > 202.0) arrow = "⬋";
        else if (angle <= 202.0 && angle > 157.5) arrow = "⬇";
        else if (angle <= 157.5 && angle > 112.5) arrow = "⬊";
        else if (angle <= 112.5 && angle > 067.5) arrow = "→";
        else if (angle <= 067.5) arrow = "⬈";

        return arrow;
    }
}