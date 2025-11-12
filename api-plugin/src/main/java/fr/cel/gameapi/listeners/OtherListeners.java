package fr.cel.gameapi.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.jetbrains.annotations.NotNull;

public final class OtherListeners implements Listener {

    // Avoid sniffer egg growth
    @EventHandler
    public void onBlockGrow(@NotNull final BlockGrowEvent event) {
        if (event.getBlock().getType() == Material.SNIFFER_EGG) event.setCancelled(true);
    }

    // Avoid snow layers formation
    @EventHandler
    public void onBlockForm(@NotNull final BlockFormEvent event) {
        if (event.getNewState().getType().name().contains("SNOW")) event.setCancelled(true);
    }

}