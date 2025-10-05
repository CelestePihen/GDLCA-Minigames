package fr.cel.valocraft.arena.state.provider.game;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class TimeOverListenerProvider extends StateListenerProvider {

    public TimeOverListenerProvider(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        if (event.getCause() == DamageCause.PROJECTILE) arena.eliminate(player);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        // TODO SMOKE - à refaire
//        Block block = event.getHitBlock();
//        if (block == null) return;
//
//        Location location = block.getLocation();
//        for (int i = 0; i < 10; i++) {
//            double offsetX = Math.random() * 4 - 2;
//            double offsetY = Math.random() * 4 - 2;
//            double offsetZ = Math.random() * 4 - 2;
//            location.add(offsetX, offsetY, offsetZ);
//            location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 1);
//            location.subtract(offsetX, offsetY, offsetZ);
//        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        if (!arena.getAttackers().team().containsPlayer(player)) return;
        if (event.getItemDrop().getItemStack().getType() != Material.BREWING_STAND) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        final Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        if (!arena.getAttackers().team().containsPlayer(player)) return;
        if (event.getItem().getItemStack().getType() != Material.BREWING_STAND) event.setCancelled(true);
    }
    
}