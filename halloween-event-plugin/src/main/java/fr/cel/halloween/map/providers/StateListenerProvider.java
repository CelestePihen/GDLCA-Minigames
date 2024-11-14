package fr.cel.halloween.map.providers;

import fr.cel.gameapi.GameAPI;
import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.inventories.ShopSoulsInventory;
import fr.cel.halloween.inventories.ShopTrackerInventory;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.timer.game.RemoveBlindnessTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public abstract class StateListenerProvider implements Listener {

    protected final HalloweenMap map;

    private final String[] jumpscareTexts = new String[] { "\uE001", "\uE002", "\uE003", "\uE004", "\uE005", "\uE006" };

    public StateListenerProvider(HalloweenMap map) {
        this.map = map;
    }

    public void onEnable(HalloweenEvent main) {
        main = map.getGameManager().getMain();
        main.getServer().getPluginManager().registerEvents(this, main);
    }
   
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player leaver = event.getPlayer();
        if (!map.isPlayerInMap(leaver)) return;
        map.removePlayer(leaver);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!map.isPlayerInMap(player)) return;
        if (!event.getMessage().contains("/hub")) return;
        map.removePlayer(player);
    }

    @EventHandler
    public void onSwappedItem(PlayerSwapHandItemsEvent event) {
        if (!map.isPlayerInMap(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if(!map.isPlayerInMap(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!map.isPlayerInMap(player)) return;

        Action action = event.getAction();

        ItemStack itemStack = event.getItem();
        if (itemStack != null && itemStack.getItemMeta() != null && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            Material type = event.getItem().getType();

            if (type == Material.POTION) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 12*20, 0, false, false, false));
                player.getInventory().remove(Material.POTION);
                return;
            }

            if (type == Material.GOLD_NUGGET) {
                map.getSoulsCollected().put(player.getUniqueId(), map.getSoulsCollected().get(player.getUniqueId()) + 5);

                player.setLevel(map.getSoulsCollected().get(player.getUniqueId()));
                player.getInventory().addItem(new ItemStack(Material.NETHER_WART, 5));

                player.getInventory().remove(Material.GOLD_NUGGET);
                return;
            }

            if (type == Material.CHEST) {
                GameAPI.getInstance().getInventoryManager().openInventory(new ShopSoulsInventory(map), player);
                player.getInventory().remove(Material.CHEST);
                return;
            }

            if (type == Material.GOLD_INGOT) {
                Collections.shuffle(map.getSouls());

                UUID victimUUID = map.getSouls().getFirst();

                if (map.getSoulsCollected().get(victimUUID) <= 3) return;

                map.getSoulsCollected().put(victimUUID, map.getSoulsCollected().get(victimUUID) - 4);
                player.getInventory().remove(Material.GOLD_INGOT);

                Player victim = Bukkit.getPlayer(victimUUID);
                if (victim == null) return;
                victim.getInventory().remove(Material.NETHER_WART);
                victim.getInventory().addItem(new ItemStack(Material.NETHER_WART, map.getSoulsCollected().get(victimUUID)));
                victim.setLevel(map.getSoulsCollected().get(victimUUID));
                return;
            }

            if (type == Material.GHAST_TEAR) {
                player.removePotionEffect(PotionEffectType.BLINDNESS);

                new RemoveBlindnessTask(map).runTaskTimer(map.getGameManager().getMain(), 0, 20);

                player.getInventory().remove(Material.GHAST_TEAR);

                for (UUID uuid : map.getSouls()) {
                    Player pl = Bukkit.getPlayer(uuid);
                    if (pl == null) continue;
                    player.hidePlayer(map.getGameManager().getMain(), pl);
                }

                return;
            }

            if (type == Material.SKELETON_SKULL) {
                player.getInventory().remove(Material.SKELETON_SKULL);

                for (UUID uuid : map.getPlayers()) {
                    Player pl = Bukkit.getPlayer(uuid);
                    if (pl == null) continue;
                    pl.sendTitle(jumpscareTexts[new Random().nextInt(jumpscareTexts.length)], "", 10, 70, 20);
                }
            }
        }

        Block block = event.getClickedBlock();
        if (block != null) {
            Material blockType = block.getType();

            if (action == Action.PHYSICAL && blockType == Material.FARMLAND) {
                event.setCancelled(true);
                return;
            }

            if (action == Action.RIGHT_CLICK_BLOCK && blockType == Material.CHEST) {
                if (map.getActiveChests().contains(block.getLocation())) {
                    if (map.getSouls().contains(player.getUniqueId())) {
                        GameAPI.getInstance().getInventoryManager().openInventory(new ShopSoulsInventory(map, block.getLocation()), player);
                    }

                    else if (map.getTracker().contains(player.getUniqueId())) {
                        GameAPI.getInstance().getInventoryManager().openInventory(new ShopTrackerInventory(map, block.getLocation()), player);
                    }
                    event.setCancelled(true);
                } else {
                    event.setCancelled(true);
                }
            }

            if (blockType == Material.FLOWER_POT || block.getType().name().startsWith("POTTED_") || (blockType == Material.CAVE_VINES || blockType == Material.CAVE_VINES_PLANT) ||
                    blockType == Material.SWEET_BERRY_BUSH || blockType == Material.HOPPER || blockType == Material.FURNACE || blockType == Material.BLAST_FURNACE ||
                    blockType == Material.SMOKER || blockType == Material.BARREL || blockType == Material.DISPENSER || blockType == Material.DROPPER ||
                    blockType == Material.CHEST_MINECART || blockType == Material.HOPPER_MINECART || blockType == Material.FURNACE_MINECART || blockType == Material.CRAFTING_TABLE ||
                    blockType == Material.DRAGON_EGG || blockType == Material.DECORATED_POT) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;

        if (map.getTracker().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (event.getItem().getItemStack().getType() == Material.NETHER_WART) {
            UUID uuid = player.getUniqueId();

            map.getSoulsCollected().put(uuid, map.getSoulsCollected().get(uuid) + 1);
            player.setLevel(map.getSoulsCollected().get(uuid));
        }

    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!map.isPlayerInMap(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleCollision(VehicleEntityCollisionEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

}