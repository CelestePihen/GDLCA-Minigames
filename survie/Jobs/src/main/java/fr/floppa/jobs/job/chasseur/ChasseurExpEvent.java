package fr.floppa.jobs.job.chasseur;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ChasseurExpEvent implements Listener {

    private final ChasseurManager manager;

    public ChasseurExpEvent(ChasseurManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onKillMob(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        // Récupérer le niveau du joueur en tant que Chasseur
        Chasseur chasseur = manager.getChasseur(player);
        int chasseurLevel = chasseur.getLevel();

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.BLAZE) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 65;
            } else if (chasseurLevel <= 10) {
                xpGained = 35;
            } else if (chasseurLevel <= 15) {
                xpGained = 28;
            } else if (chasseurLevel <= 20) {
                xpGained = 15;
            } else if (chasseurLevel <= 25) {
                xpGained = 9;
            } else {
                xpGained = 4;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.BOGGED) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 74;
            } else if (chasseurLevel <= 10) {
                xpGained = 41;
            } else if (chasseurLevel <= 15) {
                xpGained = 13;
            } else if (chasseurLevel <= 20) {
                xpGained = 5;
            } else if (chasseurLevel <= 25) {
                xpGained = 3;
            } else {
                xpGained = 1;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.BREEZE) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 55;
            } else if (chasseurLevel <= 10) {
                xpGained = 45;
            } else if (chasseurLevel <= 15) {
                xpGained = 35;
            } else if (chasseurLevel <= 20) {
                xpGained = 10;
            } else if (chasseurLevel <= 25) {
                xpGained = 8;
            } else {
                xpGained = 6;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.CREAKING) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 74;
            } else if (chasseurLevel <= 10) {
                xpGained = 41;
            } else if (chasseurLevel <= 15) {
                xpGained = 13;
            } else if (chasseurLevel <= 20) {
                xpGained = 5;
            } else if (chasseurLevel <= 25) {
                xpGained = 3;
            } else {
                xpGained = 1;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.CREEPER) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 30;
            } else if (chasseurLevel <= 10) {
                xpGained = 18;
            } else if (chasseurLevel <= 15) {
                xpGained = 12;
            } else if (chasseurLevel <= 20) {
                xpGained = 6;
            } else if (chasseurLevel <= 25) {
                xpGained = 3;
            } else {
                xpGained = 1;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.ELDER_GUARDIAN) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 95;
            } else if (chasseurLevel <= 10) {
                xpGained = 65;
            } else if (chasseurLevel <= 15) {
                xpGained = 38;
            } else if (chasseurLevel <= 20) {
                xpGained = 15;
            } else if (chasseurLevel <= 25) {
                xpGained = 8;
            } else {
                xpGained = 4;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 1780;
            } else if (chasseurLevel <= 10) {
                xpGained = 1410;
            } else if (chasseurLevel <= 15) {
                xpGained = 813;
            } else if (chasseurLevel <= 20) {
                xpGained = 445;
            } else if (chasseurLevel <= 25) {
                xpGained = 203;
            } else {
                xpGained = 150;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.ENDERMAN) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 184;
            } else if (chasseurLevel <= 10) {
                xpGained = 151;
            } else if (chasseurLevel <= 15) {
                xpGained = 130;
            } else if (chasseurLevel <= 20) {
                xpGained = 65;
            } else if (chasseurLevel <= 25) {
                xpGained = 53;
            } else {
                xpGained = 41;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.ENDERMITE) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 84;
            } else if (chasseurLevel <= 10) {
                xpGained = 41;
            } else if (chasseurLevel <= 15) {
                xpGained = 13;
            } else if (chasseurLevel <= 20) {
                xpGained = 5;
            } else if (chasseurLevel <= 25) {
                xpGained = 3;
            } else {
                xpGained = 1;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.EVOKER) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 155;
            } else if (chasseurLevel <= 10) {
                xpGained = 91;
            } else if (chasseurLevel <= 15) {
                xpGained = 53;
            } else if (chasseurLevel <= 20) {
                xpGained = 35;
            } else if (chasseurLevel <= 25) {
                xpGained = 23;
            } else {
                xpGained = 15;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.BOGGED) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 174;
            } else if (chasseurLevel <= 10) {
                xpGained = 61;
            } else if (chasseurLevel <= 15) {
                xpGained = 43;
            } else if (chasseurLevel <= 20) {
                xpGained = 35;
            } else if (chasseurLevel <= 25) {
                xpGained = 23;
            } else {
                xpGained = 11;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.GUARDIAN) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 674;
            } else if (chasseurLevel <= 10) {
                xpGained = 541;
            } else if (chasseurLevel <= 15) {
                xpGained = 313;
            } else if (chasseurLevel <= 20) {
                xpGained = 205;
            } else if (chasseurLevel <= 25) {
                xpGained = 103;
            } else {
                xpGained = 78;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.HOGLIN) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 274;
            } else if (chasseurLevel <= 10) {
                xpGained = 141;
            } else if (chasseurLevel <= 15) {
                xpGained = 93;
            } else if (chasseurLevel <= 20) {
                xpGained = 55;
            } else if (chasseurLevel <= 25) {
                xpGained = 33;
            } else {
                xpGained = 19;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.HUSK) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 64;
            } else if (chasseurLevel <= 10) {
                xpGained = 41;
            } else if (chasseurLevel <= 15) {
                xpGained = 13;
            } else if (chasseurLevel <= 20) {
                xpGained = 5;
            } else if (chasseurLevel <= 25) {
                xpGained = 3;
            } else {
                xpGained = 1;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.MAGMA_CUBE) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 174;
            } else if (chasseurLevel <= 10) {
                xpGained = 141;
            } else if (chasseurLevel <= 15) {
                xpGained = 93;
            } else if (chasseurLevel <= 20) {
                xpGained = 45;
            } else if (chasseurLevel <= 25) {
                xpGained = 23;
            } else {
                xpGained = 11;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.PHANTOM) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 5;
            } else if (chasseurLevel <= 10) {
                xpGained = 4;
            } else if (chasseurLevel <= 15) {
                xpGained = 3;
            } else if (chasseurLevel <= 20) {
                xpGained = 2;
            } else if (chasseurLevel <= 25) {
                xpGained = 1;
            } else {
                xpGained = 1;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.PIGLIN_BRUTE) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 205;
            } else if (chasseurLevel <= 10) {
                xpGained = 174;
            } else if (chasseurLevel <= 15) {
                xpGained = 155;
            } else if (chasseurLevel <= 20) {
                xpGained = 121;
            } else if (chasseurLevel <= 25) {
                xpGained = 100;
            } else {
                xpGained = 75;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.PILLAGER) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 85;
            } else if (chasseurLevel <= 10) {
                xpGained = 64;
            } else if (chasseurLevel <= 15) {
                xpGained = 43;
            } else if (chasseurLevel <= 20) {
                xpGained = 22;
            } else if (chasseurLevel <= 25) {
                xpGained = 11;
            } else {
                xpGained = 6;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.RAVAGER) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 305;
            } else if (chasseurLevel <= 10) {
                xpGained = 204;
            } else if (chasseurLevel <= 15) {
                xpGained = 103;
            } else if (chasseurLevel <= 20) {
                xpGained = 65;
            } else if (chasseurLevel <= 25) {
                xpGained = 22;
            } else {
                xpGained = 12;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.SHULKER) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 345;
            } else if (chasseurLevel <= 10) {
                xpGained = 244;
            } else if (chasseurLevel <= 15) {
                xpGained = 203;
            } else if (chasseurLevel <= 20) {
                xpGained = 182;
            } else if (chasseurLevel <= 25) {
                xpGained = 140;
            } else {
                xpGained = 70;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.SILVERFISH) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 95;
            } else if (chasseurLevel <= 10) {
                xpGained = 84;
            } else if (chasseurLevel <= 15) {
                xpGained = 73;
            } else if (chasseurLevel <= 20) {
                xpGained = 62;
            } else if (chasseurLevel <= 25) {
                xpGained = 51;
            } else {
                xpGained = 40;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.SKELETON) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 55;
            } else if (chasseurLevel <= 10) {
                xpGained = 44;
            } else if (chasseurLevel <= 15) {
                xpGained = 33;
            } else if (chasseurLevel <= 20) {
                xpGained = 22;
            } else if (chasseurLevel <= 25) {
                xpGained = 11;
            } else {
                xpGained = 1;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.SLIME) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 35;
            } else if (chasseurLevel <= 10) {
                xpGained = 30;
            } else if (chasseurLevel <= 15) {
                xpGained = 25;
            } else if (chasseurLevel <= 20) {
                xpGained = 20;
            } else if (chasseurLevel <= 25) {
                xpGained = 15;
            } else {
                xpGained = 10;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.STRAY) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 65;
            } else if (chasseurLevel <= 10) {
                xpGained = 54;
            } else if (chasseurLevel <= 15) {
                xpGained = 43;
            } else if (chasseurLevel <= 20) {
                xpGained = 32;
            } else if (chasseurLevel <= 25) {
                xpGained = 21;
            } else {
                xpGained = 11;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.VEX) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 85;
            } else if (chasseurLevel <= 10) {
                xpGained = 74;
            } else if (chasseurLevel <= 15) {
                xpGained = 53;
            } else if (chasseurLevel <= 20) {
                xpGained = 32;
            } else if (chasseurLevel <= 25) {
                xpGained = 21;
            } else {
                xpGained = 10;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.VINDICATOR) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 165;
            } else if (chasseurLevel <= 10) {
                xpGained = 64;
            } else if (chasseurLevel <= 15) {
                xpGained = 33;
            } else if (chasseurLevel <= 20) {
                xpGained = 22;
            } else if (chasseurLevel <= 25) {
                xpGained = 11;
            } else {
                xpGained = 5;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.WARDEN) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 7000;
            } else if (chasseurLevel <= 10) {
                xpGained = 4000;
            } else if (chasseurLevel <= 15) {
                xpGained = 3000;
            } else if (chasseurLevel <= 20) {
                xpGained = 2000;
            } else if (chasseurLevel <= 25) {
                xpGained = 1000;
            } else {
                xpGained = 750;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.WITCH) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 105;
            } else if (chasseurLevel <= 10) {
                xpGained = 84;
            } else if (chasseurLevel <= 15) {
                xpGained = 53;
            } else if (chasseurLevel <= 20) {
                xpGained = 29;
            } else if (chasseurLevel <= 25) {
                xpGained = 17;
            } else {
                xpGained = 8;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.WITHER) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 805;
            } else if (chasseurLevel <= 10) {
                xpGained = 705;
            } else if (chasseurLevel <= 15) {
                xpGained = 500;
            } else if (chasseurLevel <= 20) {
                xpGained = 325;
            } else if (chasseurLevel <= 25) {
                xpGained = 165;
            } else {
                xpGained = 100;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.WITHER_SKELETON) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 205;
            } else if (chasseurLevel <= 10) {
                xpGained = 184;
            } else if (chasseurLevel <= 15) {
                xpGained = 163;
            } else if (chasseurLevel <= 20) {
                xpGained = 102;
            } else if (chasseurLevel <= 25) {
                xpGained = 41;
            } else {
                xpGained = 30;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.ZOGLIN) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 274;
            } else if (chasseurLevel <= 10) {
                xpGained = 141;
            } else if (chasseurLevel <= 15) {
                xpGained = 93;
            } else if (chasseurLevel <= 20) {
                xpGained = 55;
            } else if (chasseurLevel <= 25) {
                xpGained = 33;
            } else {
                xpGained = 19;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.ZOMBIE) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 25;
            } else if (chasseurLevel <= 10) {
                xpGained = 15;
            } else if (chasseurLevel <= 15) {
                xpGained = 10;
            } else if (chasseurLevel <= 20) {
                xpGained = 8;
            } else if (chasseurLevel <= 25) {
                xpGained = 6;
            } else {
                xpGained = 1;
            }

            chasseur.ajouterExp(player, xpGained);
        }

        if (event.getEntity().getKiller() == player && event.getEntity().getType() == EntityType.ZOMBIE_VILLAGER) {
            int xpGained;

            if (chasseurLevel <= 5) {
                xpGained = 35;
            } else if (chasseurLevel <= 10) {
                xpGained = 25;
            } else if (chasseurLevel <= 15) {
                xpGained = 15;
            } else if (chasseurLevel <= 20) {
                xpGained = 10;
            } else if (chasseurLevel <= 25) {
                xpGained = 7;
            } else {
                xpGained = 3;
            }

            chasseur.ajouterExp(player, xpGained);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        if (manager.getChasseur(player).getLevel() >= 30){
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, -1, 0, false, false,true));
        }
    }
}