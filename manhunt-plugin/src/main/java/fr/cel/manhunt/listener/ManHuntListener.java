package fr.cel.manhunt.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.cel.manhunt.ManHunt;

public class ManHuntListener implements Listener {

    private ManHunt main;

    public ManHuntListener(ManHunt main) {
        this.main = main;
    }

    @EventHandler
    public void deathMan(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (main.getMan() == null) return;
        if (player.getName() != Bukkit.getPlayer(main.getMan().get(0)).getName()) return;

        Bukkit.getOnlinePlayers().forEach(pl -> {
            pl.sendMessage(main.getPrefix() + "Le runner a perdu la partie !");
        });
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (main.getMan() == null) return;
        if (main.getMan() == Bukkit.getPlayer(main.getMan().get(0))) main.getMan().remove(player.getUniqueId());
    }
    
}