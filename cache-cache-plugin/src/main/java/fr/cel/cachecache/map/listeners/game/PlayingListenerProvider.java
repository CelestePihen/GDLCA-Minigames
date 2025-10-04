package fr.cel.cachecache.map.listeners.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.StatisticsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

    public PlayingListenerProvider(CCMap map) {
        super(map);
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
    private void onDeathAndKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!map.isPlayerInMap(player)) return;

        event.deathMessage(Component.empty());

        if (map.getTimer() < 30) {
            map.sendMessage(Component.text("Le joueur " + player.getName() + " est mort avant les 30 secondes d'attente. Il est donc ressucité."));
        } else {
            map.eliminate(player);
        }
    }

    @EventHandler
    protected void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();

        if (entityDamager instanceof Player damager && event.getEntity() instanceof Player damaged) {
            if (!map.isPlayerInMap(damager) || !map.isPlayerInMap(damaged)) return;
            event.setCancelled(true);

            if (map.getSeekers().contains(damager.getUniqueId())) {
                GameAPI.getInstance().getStatisticsManager().updatePlayerStatistic(damager, StatisticsManager.PlayerStatistics.CC_ELIMINATIONS, 1);
                map.eliminate(damaged);
            }
            else if (map.getHiders().contains(damager.getUniqueId())) {
                map.getCheckAdvancements().giveAudacieux(damager);
            }
            return;
        }

        if (entityDamager instanceof Player pl && map.isPlayerInMap(pl)) event.setCancelled(true);
    }

    @EventHandler
    protected void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!map.isPlayerInMap(player)) return;

        // Traversée musicale
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.OAK_BUTTON && map.getMapName().equalsIgnoreCase("moulin")) {
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
                }.runTaskLater(map.getGameManager().getMain(), 20 * 15); // 300 ticks = 15 secondes
            }

            // on donne le succès s'il clique sur le 2ème bouton en moins de 15 secondes
            if (playerMoulinTimers.containsKey(playerId) && block.getLocation().equals(new Location(Bukkit.getWorld("world"), -22, 65, -75))) {
                long firstTime = playerMoulinTimers.get(playerId);
                long timeElapsed = (currentTime - firstTime) / 1000; // convertir en secondes

                if (timeElapsed <= 15) {
                    map.getCheckAdvancements().giveTraverseeMusicale(player);
                    playerMoulinTimers.remove(playerId);
                    return;
                }
            }
        }

        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        if (!itemMeta.hasItemName()) return;

        for (GroundItem groundItem : map.getAvailableGroundItems()) {
            if (groundItem == null) continue;

            ItemMeta itemMetaGI = groundItem.getItemStack().getItemMeta();
            if (itemMetaGI == null) continue;

            String itemName = ((TextComponent) itemMeta.itemName()).content();
            String itemNameGI = ((TextComponent) itemMetaGI.itemName()).content();
            if (!itemName.equals(itemNameGI)) continue;

            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                // Succès Pas besoin
                if (map.getTimer() <= 420 && map.getHiders().contains(player.getUniqueId())) {
                    // s'il a utilisé un objet et que le timer est en dessous de 7min alors, on ajoute le joueur
                    // dans la liste des joueurs qui n'ont pas le droit d'avoir le succès
                    map.getCheckAdvancements().getPlayersPasBesoin().add(player.getUniqueId());
                }

                groundItem.onInteract(player, map);
            }
        }
    }

    @EventHandler
    private void onPlayerDrop(PlayerDropItemEvent event) {
        if (!map.isPlayerInMap(event.getPlayer())) return;
        if (event.getItemDrop().getItemStack().getType() == Material.STICK) event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player) || !map.isPlayerInMap(player)) return;

        Item item = event.getItem();
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        if (itemMeta == null) return;

        if (map.getSpawnedGroundItems().contains(item.getUniqueId())) {
            map.getSpawnedGroundItems().remove(item.getUniqueId());
            player.sendMessage(map.getGameManager().getPrefix().append(Component.text("Vous avez récupéré ").append(itemMeta.itemName())));
        }
    }

    @EventHandler
    private void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !map.isPlayerInMap(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(!map.isFallDamage());
            return;
        }

        event.setCancelled(true);
    }

    /**
     * Permet de détecter si un joueur a activé un levier sur la Carte Bunker
     */
    @EventHandler
    private void onLeverAction(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.LEVER || !block.getLocation().equals(map.getLeverLocation())) return;
        boolean isPowered = event.getNewCurrent() > 0;

        for (UUID uuid : map.getHiders()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (isPowered) {
                player.getInventory().setHelmet(null);
            } else {
                player.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
            }
        }

        map.changeLamps(isPowered);
    }

    /**
     * Cet événement sert pour le succès Monter ou descendre la montagne de sable sur la carte Désert,
     * le succès Le pied sur le pouvoir, ainsi que le succès "T'es pas essouflé ?"
     */
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!map.isPlayerInMap(player)) return;

        UUID playerUUID = player.getUniqueId();

        // on s'en fout de vérifier s'il est déjà dedans, car un Set ne peut contenir qu'une seule fois le même objet
        if (map.getTimer() <= 480 && player.isSprinting()) {
            map.getCheckAdvancements().getPlayerWhoRun().add(playerUUID);
        }

        Location loc = player.getLocation();

        // Monter ou descendre la montagne de sable (Désert)
        if (map.getMapName().equalsIgnoreCase("desert")) {
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
                }.runTaskLater(map.getGameManager().getMain(), 20 * 30);
            }

            if (playerY == 58 && playerDesertTimers.containsKey(playerUUID)) {
                long firstTime = playerDesertTimers.get(playerUUID);
                long timeElapsed = (currentTime - firstTime) / 1000; // Convertir en secondes

                if (timeElapsed <= 30) {
                    map.getCheckAdvancements().giveMontagneSable(player);
                    playerDesertTimers.remove(playerUUID);
                }
            }
        }

        // Pied pouvoir
        for (Location location : map.getLocationGroundItems()) {
            if (!loc.getBlock().equals(location.getBlock())) continue;

            List<Location> piedPouvoirList = map.getCheckAdvancements().getPiedPouvoir().get(playerUUID);
            boolean dejaDansDictionnaire = piedPouvoirList.stream()
                    .anyMatch(l -> l.getBlockX() == location.getBlockX() &&
                            l.getBlockY() == location.getBlockY() &&
                            l.getBlockZ() == location.getBlockZ());

            if (!dejaDansDictionnaire) piedPouvoirList.add(location.clone());
        }
        // Pied pouvoir
    }
    
}