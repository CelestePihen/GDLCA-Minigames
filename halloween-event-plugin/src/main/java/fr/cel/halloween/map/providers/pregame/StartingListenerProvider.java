package fr.cel.halloween.map.providers.pregame;

import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.providers.StateListenerProvider;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class StartingListenerProvider extends StateListenerProvider {

    public StartingListenerProvider(HalloweenMap map) {
        super(map);
    }

    @Override
    public void onEnable(HalloweenEvent main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!map.isPlayerInMap(player)) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if (block.getType() == Material.CHEST) {
            event.setCancelled(true);
        }
    }
    
}