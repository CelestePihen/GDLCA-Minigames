package fr.floppa.jobs.job.enchanteur;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EnchanteurExpEvent implements Listener {

    private final EnchanteurManager manager;

    public EnchanteurExpEvent(EnchanteurManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        Enchanteur enchanteur = manager.getEnchanteur(player);
        int enchanteurLevel = enchanteur.getLevel();

        int levelJoueur = player.getLevel();

        int xpGained = 0;

        if (enchanteurLevel <= 5 && levelJoueur >= 30){
            xpGained = 1000;
        }
        else if (enchanteurLevel <= 5 && levelJoueur >= 20){
            xpGained = 750;
        }
        else if (enchanteurLevel <= 5 && levelJoueur >= 10) {
            xpGained = 200;
        }


        else if (enchanteurLevel <= 10 && levelJoueur >= 30) {
            xpGained = 600;
        }
        else if (enchanteurLevel <= 10 && levelJoueur >= 20) {
            xpGained = 450;
        }
        else if (enchanteurLevel <= 10 && levelJoueur >= 10) {
            xpGained = 175;
        }


        else if (enchanteurLevel <= 15 && levelJoueur >= 30) {
            xpGained = 500;
        }
        else if (enchanteurLevel <= 15 && levelJoueur >= 20) {
            xpGained = 250;
        }
        else if (enchanteurLevel <= 15 && levelJoueur >= 10) {
            xpGained = 150;
        }


        else if (enchanteurLevel <= 20 && levelJoueur >= 30) {
            xpGained = 400;
        }
        else if (enchanteurLevel <= 20 && levelJoueur >= 20) {
            xpGained = 175;
        }
        else if (enchanteurLevel <= 20 && levelJoueur >= 10) {
            xpGained = 125;
        }

        else if (enchanteurLevel <= 25 && levelJoueur >= 30) {
            xpGained = 300;
        }
        else if (enchanteurLevel <= 25 && levelJoueur >= 20) {
            xpGained = 125;
        }
        else if (enchanteurLevel <= 25 && levelJoueur >= 10) {
            xpGained = 100;
        }

        else if (enchanteurLevel <= 30 && levelJoueur >= 30) {
            xpGained = 200;
        }
        else if (enchanteurLevel <= 30 && levelJoueur >= 20) {
            xpGained = 100;
        }
        else  {
            xpGained = 50;
        }

        enchanteur.ajouterExp(player, xpGained);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        if (manager.getEnchanteur(player).getLevel() >= 30){
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 0, false, false));
        }
    }

}
