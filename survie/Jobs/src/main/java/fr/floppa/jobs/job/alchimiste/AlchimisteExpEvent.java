package fr.floppa.jobs.job.alchimiste;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AlchimisteExpEvent implements Listener {

    private final AlchimisteManager manager;

    public AlchimisteExpEvent(AlchimisteManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        for (ItemStack itemStack : event.getResults()) {
            if (itemStack == null) continue;
            Location location = event.getBlock().getLocation();

            for (Entity entity : event.getBlock().getWorld().getNearbyEntities(location, 4, 4, 4)) {
                if (entity instanceof Player player) {
                    Alchimiste alchimiste = manager.getAlchimiste(player);
                    int alchimisteLevel = alchimiste.getLevel();

                    int xpGained;

                    if (alchimisteLevel <= 5) {
                        xpGained = 280;
                    } else if (alchimisteLevel <= 10) {
                        xpGained = 270;
                    } else if (alchimisteLevel <= 15) {
                        xpGained = 260;
                    } else if (alchimisteLevel <= 20) {
                        xpGained = 250;
                    } else if (alchimisteLevel <= 25) {
                        xpGained = 240;
                    } else {
                        xpGained = 230;
                    }

                    alchimiste.ajouterExp(player, xpGained);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (manager.getAlchimiste(player).getLevel() >= 30){
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 0, false, false));
        }
    }
}
