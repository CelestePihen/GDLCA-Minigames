package fr.cel.hub.listener;

import fr.cel.gameapi.GameAPI;
import fr.cel.hub.Hub;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerListener extends HListener {

    public PlayerListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || !GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player) || player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager) || event.getEntity() instanceof Player) return;
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(damager) || damager.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)
                || player.isOp() || event.getClickedBlock() == null) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                || event.getAction() == Action.PHYSICAL) event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player
                && GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || player.isOp()) return;

        if (event.getClickedInventory() == null) return;

        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
            return;
        }

        if (event.isShiftClick() && event.getCurrentItem() != null && isArmorItem(event.getCurrentItem())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractOnArmor(PlayerInteractEvent event) {
        if (!event.getPlayer().isOp() && event.getItem() != null && isArmorItem(event.getItem())) event.setCancelled(true);
    }

    private boolean isArmorItem(@NotNull ItemStack item) {
        if (item.getType().isAir()) return false;

        return Tag.ITEMS_HEAD_ARMOR.isTagged(item.getType())
                || Tag.ITEMS_CHEST_ARMOR.isTagged(item.getType())
                || Tag.ITEMS_LEG_ARMOR.isTagged(item.getType())
                || Tag.ITEMS_FOOT_ARMOR.isTagged(item.getType())
                || item.getType() == Material.PAPER;
    }
}