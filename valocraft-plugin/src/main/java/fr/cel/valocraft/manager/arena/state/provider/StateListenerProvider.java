package fr.cel.valocraft.manager.arena.state.provider;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.ValoArena;

public class StateListenerProvider implements Listener {

    protected final ValoArena arena;

    public StateListenerProvider(ValoArena arena) {
        this.arena = arena;
    }

    public void onEnable(ValoCraft main) {
        main = arena.getGameManager().getMain();
        main.getServer().getPluginManager().registerEvents(this, main);
    }
   
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        arena.removePlayer(player);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equals("/hub")) {
            if (arena.isPlayerInArena(player)) arena.removePlayer(player);
        }
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) if (arena.isPlayerInArena(player)) event.setCancelled(true);
    }

}
