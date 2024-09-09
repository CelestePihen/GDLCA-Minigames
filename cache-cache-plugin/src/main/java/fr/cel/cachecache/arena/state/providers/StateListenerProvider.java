package fr.cel.cachecache.arena.state.providers;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.state.game.PlayingArenaState;
import fr.cel.cachecache.arena.state.pregame.PreGameArenaState;
import fr.cel.cachecache.arena.state.pregame.StartingArenaState;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        final Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        if (!event.getMessage().contains("/hub")) return;
        arena.removePlayer(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;

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
                type == Material.CHEST_MINECART || type == Material.HOPPER_MINECART || type == Material.FURNACE_MINECART || type == Material.CRAFTING_TABLE) {
            event.setCancelled(true);
            return;
        }

        if (type == Material.LEVER && arena.getArenaState() instanceof PlayingArenaState && isLeverLocation(block.getLocation())) {
            if (block.getBlockData() instanceof Powerable powerable) {
                if (powerable.isPowered()) {
                    arena.getHiders().forEach(uuid -> Bukkit.getPlayer(uuid).getInventory().setHelmet(new ItemStack(Material.AIR)));
                    arena.getSeekers().forEach(uuid -> Bukkit.getPlayer(uuid).setGlowing(true));
                } else {
                    arena.getHiders().forEach(uuid -> Bukkit.getPlayer(uuid).getInventory().setHelmet(new ItemBuilder(Material.CARVED_PUMPKIN).setDisplayName("Masque").toItemStack()));
                    arena.getSeekers().forEach(uuid -> Bukkit.getPlayer(uuid).setGlowing(false));
                }
            }
        }

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

        for (GroundItem groundItem : arena.getAvailableGroundItems()) {
            if (groundItem != null && itemMeta.getDisplayName().equalsIgnoreCase(groundItem.getDisplayName())) {
                groundItem.onInteract(player, arena);
            }
        }

    }

    private boolean isLeverLocation(Location locationBlock) {
        boolean b = false;
        if (locationBlock.getBlockX() == arena.getLeverLocation().getBlockX()) {
            if (locationBlock.getBlockY() == arena.getLeverLocation().getBlockY()) {
                if (locationBlock.getBlockZ() == arena.getLeverLocation().getBlockZ()) {
                    b = true;
                }
            }
        }
        return b;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (itemStack.getType() == Material.CARVED_PUMPKIN) {
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