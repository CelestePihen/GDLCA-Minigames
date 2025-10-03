package fr.cel.gameapi.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class OtherListeners implements Listener {

    @EventHandler
    private void onBlockGrow(BlockGrowEvent event) {
        if (event.getBlock().getType() == Material.SNIFFER_EGG) event.setCancelled(true);
    }

}
