package fr.floppa.jobs.job.explorateur;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ExplorateurExpEvent implements Listener {

    private final ExplorateurManager manager;

    public ExplorateurExpEvent(ExplorateurManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onWalk(PlayerMoveEvent event){
        Location from = event.getFrom();

        Location to = event.getTo();
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        Player player = event.getPlayer();
        Explorateur explorateur = manager.getExplorateur(player);
        int explorateurLevel = explorateur.getLevel();

        int xpGained;
        if (explorateurLevel <= 1000){
            xpGained = 1;

            explorateur.ajouterExp(player, xpGained);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        if (manager.getExplorateur(player).getLevel() >= 30){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false,true));
        }
    }
}
