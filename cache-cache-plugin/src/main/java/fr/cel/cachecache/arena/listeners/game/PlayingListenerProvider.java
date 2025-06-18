package fr.cel.cachecache.arena.listeners.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.listeners.StateListenerProvider;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.StatisticsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayingListenerProvider extends StateListenerProvider {

    // succès désert
    private final Map<UUID, Long> playerDesertTimers = new HashMap<>();
    private final Map<UUID, Long> playerMoulinTimers = new HashMap<>();

    public PlayingListenerProvider(CCArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onDeathAndKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!arena.isPlayerInArena(player)) return;

        event.setDeathMessage("");

        if (arena.getTimer() < 30) {
            arena.sendMessage("Le joueur " + player.getName() + " est mort avant les 30 secondes d'attente. Il est donc ressucité.");
        } else {
            arena.eliminate(player);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();

        if (entityDamager instanceof Player damager && event.getEntity() instanceof Player damaged) {
            if (!arena.isPlayerInArena(damager) || !arena.isPlayerInArena(damaged)) return;
            event.setCancelled(true);

            if (arena.getSeekers().contains(damager.getUniqueId())) {
                GameAPI.getInstance().getStatisticsManager().updatePlayerStatistic(damager, StatisticsManager.PlayerStatistics.CC_ELIMINATIONS, 1);
                arena.eliminate(damaged);
            }
            else if (arena.getHiders().contains(damager.getUniqueId())) {
                arena.getCheckAdvancements().giveAudacieux(damager);
            }
            return;
        }

        if (entityDamager instanceof Player pl && arena.isPlayerInArena(pl)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;

        // Traversée musicale
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.OAK_BUTTON && arena.getArenaName().equalsIgnoreCase("moulin")) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();

            if (!playerMoulinTimers.containsKey(playerId) && block.getLocation().equals(new Location(Bukkit.getWorld("world"), -58, 46, -51))) {
                // on démarre le timer s'il clique sur le 1er bouton
                playerMoulinTimers.put(playerId, currentTime);

                // lance un chrono qui supprimera le timer du joueur s'il n'a pas cliqué sur l'autre bouton en moins de 30 secondes
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        playerMoulinTimers.remove(playerId);
                    }
                }.runTaskLater(arena.getGameManager().getMain(), 20 * 15); // 300 ticks = 15 secondes
            }

            // on donne le succès s'il clique sur le 2ème bouton en moins de 15 secondes
            if (playerMoulinTimers.containsKey(playerId) && block.getLocation().equals(new Location(Bukkit.getWorld("world"), -22, 65, -75))) {
                long firstTime = playerMoulinTimers.get(playerId);
                long timeElapsed = (currentTime - firstTime) / 1000; // convertir en secondes

                if (timeElapsed <= 15) {
                    arena.getCheckAdvancements().giveTraverseeMusicale(player);
                    playerMoulinTimers.remove(playerId);
                    return;
                }
            }
        }
        // Traversée musicale

        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        if (itemStack.getItemMeta() == null) return;

        for (GroundItem groundItem : arena.getAvailableGroundItems()) {
            if (groundItem == null) continue;
            if (groundItem.getItemStack().getItemMeta() == null) continue;
            if (!itemStack.getItemMeta().getItemName().equals(groundItem.getItemStack().getItemMeta().getItemName())) continue;

            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                // Succès Pas besoin
                if (arena.getTimer() <= 420 && arena.getHiders().contains(player.getUniqueId())) {
                    // s'il a utilisé un objet et que le timer est en dessous de 7min alors, on ajoute l'ID du joueur
                    // dans la liste des joueurs qui n'ont pas le droit d'avoir le succès
                    arena.getCheckAdvancements().getPlayersPasBesoin().add(player.getUniqueId());
                }
                // Succès Pas besoin

                groundItem.onInteract(player, arena);
            }
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (!arena.isPlayerInArena(event.getPlayer())) return;
        if (event.getItemDrop().getItemStack().getType() == Material.STICK) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player) || !arena.isPlayerInArena(player)) return;

        Item item = event.getItem();
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        if (itemMeta == null) return;

        if (arena.getSpawnedGroundItems().contains(item)) {
            arena.getSpawnedGroundItems().remove(item);
            player.sendMessage(arena.getGameManager().getPrefix() + "Vous avez récupéré " + itemMeta.getItemName());
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !arena.isPlayerInArena(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(!arena.isFallDamage());
            return;
        }

        event.setCancelled(true);
    }

    /**
     * Permet de détecter si un joueur a activé un levier sur la Carte Bunker
     */
    @EventHandler
    public void onLeverAction(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.LEVER || !block.getLocation().equals(arena.getLeverLocation())) return;
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

        arena.changeLamps(isPowered);
    }

    /**
     * Cet événement sert pour le succès Monter ou descendre la montagne de sable sur la carte Désert, le succès Le pied sur le pouvoir ainsi que le succès "T'es pas essouflé ?"
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;

        UUID playerUUID = player.getUniqueId();

        // on s'en fout de vérifier s'il est déjà dedans, car un Set ne peut contenir qu'une seule fois le même objet
        if (arena.getTimer() <= 480 && player.isSprinting()) {
            arena.getCheckAdvancements().getPlayerWhoRun().add(playerUUID);
        }

        Location loc = player.getLocation();

        // Monter ou descendre la montagne de sable (Désert)
        if (arena.getArenaName().equalsIgnoreCase("desert")) {
            int playerY = loc.getBlockY();
            long currentTime = System.currentTimeMillis();

            if (playerY == 88) {
                if (playerDesertTimers.containsKey(playerUUID)) return;
                playerDesertTimers.put(playerUUID, currentTime);

                // lance un chrono qui supprimera le timer du joueur s'il n'a pas atteint l'autre hauteur en moins de 30 secondes
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        playerDesertTimers.remove(playerUUID);
                    }
                }.runTaskLater(arena.getGameManager().getMain(), 20 * 30);
            }

            if (playerY == 58 && playerDesertTimers.containsKey(playerUUID)) {
                long firstTime = playerDesertTimers.get(playerUUID);
                long timeElapsed = (currentTime - firstTime) / 1000; // Convertir en secondes

                if (timeElapsed <= 30) {
                    arena.getCheckAdvancements().giveMontagneSable(player);
                    playerDesertTimers.remove(playerUUID);
                }
            }
        }

        // Pied pouvoir
        for (Location location : arena.getLocationGroundItems()) {
            if (!loc.getBlock().equals(location.getBlock())) continue;

            List<Location> piedPouvoirList = arena.getCheckAdvancements().getPiedPouvoir().get(playerUUID);
            boolean dejaDansDictionnaire = piedPouvoirList.stream()
                    .anyMatch(l -> l.getBlockX() == location.getBlockX() &&
                            l.getBlockY() == location.getBlockY() &&
                            l.getBlockZ() == location.getBlockZ());

            if (!dejaDansDictionnaire) piedPouvoirList.add(location.clone());
        }
        // Pied pouvoir
    }
    
}