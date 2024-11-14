package fr.cel.cachecache.arena.providers;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.state.pregame.PreGameArenaState;
import fr.cel.cachecache.arena.state.pregame.StartingArenaState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public abstract class StateListenerProvider implements Listener {

    protected final CCArena arena;

    public StateListenerProvider(CCArena arena) {
        this.arena = arena;
    }

    public void onEnable(CacheCache main) {
        main = arena.getGameManager().getMain();
        main.getServer().getPluginManager().registerEvents(this, main);
    }
   
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player leaver = event.getPlayer();
        if (!arena.isPlayerInArena(leaver)) return;
        arena.removePlayer(leaver);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        if (!event.getMessage().contains("/hub")) return;
        arena.removePlayer(player);
    }

    @EventHandler
    public void onLeverAction(BlockRedstoneEvent event) {
        if (event.getBlock().getType() != Material.LEVER) return;
        if (!event.getBlock().getLocation().equals(arena.getLeverLocation())) return;

        boolean isPowered = event.getNewCurrent() > 0;

        for (UUID uuid : arena.getHiders()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (isPowered) {
                player.getInventory().setHelmet(null);
            } else {
                player.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
            }
        }

        for (UUID uuid : arena.getSeekers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            player.setGlowing(isPowered);
        }

        arena.changeLamps(isPowered);
    }

    @EventHandler
    public void onSwappedItem(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(!arena.isPlayerInArena(player)) return;

        if (event.getItemDrop().getItemStack().getType() == Material.AMETHYST_SHARD)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        if (itemMeta.getDisplayName().equalsIgnoreCase("Démarrer la partie")) {
            if (arena.getArenaState() instanceof PreGameArenaState) {
                if (arena.getHunterMode() == CCArena.HunterMode.TwoHuntersAtStart) {
                    if (arena.getPlayers().size() <= 2) {
                        player.sendMessage(arena.getGameManager().getPrefix() + "Il n'y a pas assez de joueurs (minimum 3 joueurs) !");
                    } else {
                        arena.setArenaState(new StartingArenaState(arena));
                    }
                }

                else {
                    if (arena.getPlayers().size() < 2) {
                        player.sendMessage(arena.getGameManager().getPrefix() + "Il n'y a pas assez de joueurs (minimum 2 joueurs) !");
                    } else {
                        arena.setArenaState(new StartingArenaState(arena));
                    }
                }

            } else {
                player.sendMessage(arena.getGameManager().getPrefix() + "La partie est déjà lancée.");
            }
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) return;

        Material type = block.getType();

        if (event.getAction() == Action.PHYSICAL && type == Material.FARMLAND) {
            event.setCancelled(true);
            return;
        }

        if (type == Material.FLOWER_POT || block.getType().name().startsWith("POTTED_") || (type == Material.CAVE_VINES || type == Material.CAVE_VINES_PLANT) ||
                type == Material.SWEET_BERRY_BUSH || type == Material.CHEST ||  type == Material.HOPPER || type == Material.FURNACE || type == Material.BLAST_FURNACE ||
                type == Material.SMOKER || type == Material.BARREL || type == Material.DISPENSER || type == Material.DROPPER ||
                type == Material.CHEST_MINECART || type == Material.HOPPER_MINECART || type == Material.FURNACE_MINECART || type == Material.CRAFTING_TABLE ||
                type == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (itemStack.getType() == Material.CARVED_PUMPKIN || itemStack.getType() == Material.AMETHYST_SHARD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleEntityCollisionEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        if (player.isOp()) return;

        if (event.getInventory().getHolder() instanceof Entity entity) {
            if (entity.getType() == EntityType.CHEST_MINECART || entity.getType() == EntityType.FURNACE_MINECART || entity.getType() == EntityType.HOPPER_MINECART) {
                event.setCancelled(true);
            }
        }
    }

}