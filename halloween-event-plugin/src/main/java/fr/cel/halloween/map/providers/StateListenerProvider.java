package fr.cel.halloween.map.providers;

import fr.cel.gameapi.GameAPI;
import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.inventories.ShopSoulsInventory;
import fr.cel.halloween.inventories.ShopTrackerInventory;
import fr.cel.halloween.manager.GameManager;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.timer.game.RemoveBlindnessTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
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

    private static final Random RANDOM = new Random();
    private static final String[] JUMPSCARE_TEXTS = new String[] { "\uE001", "\uE002", "\uE003", "\uE004", "\uE005", "\uE006" };

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
        if (map.isPlayerInMap(leaver)) map.removePlayer(leaver);
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
        if (map.isPlayerInMap(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (map.isPlayerInMap(event.getPlayer())) event.setCancelled(true);
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
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 12*20, 0, false, false, false));
                return;
            }

            if (type == Material.GOLD_NUGGET) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

                map.getSoulsCollected().put(player.getUniqueId(), map.getSoulsCollected().get(player.getUniqueId()) + 5);

                player.setLevel(map.getSoulsCollected().get(player.getUniqueId()));
                player.getInventory().addItem(new ItemStack(Material.NETHER_WART, 5));
                return;
            }

            if (type == Material.CHEST) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                GameAPI.getInstance().getInventoryManager().openInventory(new ShopSoulsInventory(map), player);
                return;
            }

            if (type == Material.GOLD_INGOT) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

                Collections.shuffle(map.getSouls());
                UUID victimUUID = map.getSouls().getFirst();

                if (map.getSoulsCollected().get(victimUUID) <= 3) {
                    player.sendMessage(GameManager.getPrefix().append(Component.text("Le joueur choisi a de la chance... Il a peu d'âmes sur lui, réessayez !")));
                    return;
                }

                map.getSoulsCollected().put(victimUUID, map.getSoulsCollected().get(victimUUID) - 4);

                Player victim = Bukkit.getPlayer(victimUUID);
                if (victim == null) return;

                int remaining = 4;
                for (ItemStack item : victim.getInventory().getContents()) {
                    if (item == null || item.getType() != Material.NETHER_WART) continue;

                    int stackAmount = item.getAmount();
                    if (stackAmount > remaining) {
                        item.setAmount(stackAmount - remaining);
                        break;
                    } else {
                        victim.getInventory().removeItem(item);
                        remaining -= stackAmount;
                        if (remaining <= 0) break;
                    }
                }

                victim.setLevel(map.getSoulsCollected().get(victimUUID));
                return;
            }

            if (type == Material.GHAST_TEAR) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

                player.removePotionEffect(PotionEffectType.BLINDNESS);

                new RemoveBlindnessTask(map).runTaskTimer(map.getGameManager().getMain(), 0, 20);

                for (UUID uuid : map.getSouls()) {
                    Player pl = Bukkit.getPlayer(uuid);
                    if (pl != null) player.hidePlayer(map.getGameManager().getMain(), pl);
                }

                return;
            }

            if (type == Material.SKELETON_SKULL) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

                for (UUID uuid : map.getPlayers()) {
                    Player pl = Bukkit.getPlayer(uuid);
                    if (pl != null) pl.showTitle(Title.title(Component.text(JUMPSCARE_TEXTS[RANDOM.nextInt(JUMPSCARE_TEXTS.length)]), Component.empty()));
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
        if (!player.isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;
        if (!player.isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleCollision(VehicleEntityCollisionEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;
        if (!player.isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;
        if (!player.isOp()) event.setCancelled(true);
    }

}