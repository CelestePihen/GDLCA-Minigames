package fr.floppa.jobs.job.mineur;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MineurExpEvent implements Listener {

    private final MineurManager manager;

    public MineurExpEvent(MineurManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();

        Mineur mineur = manager.getMineur(player);
        int mineurLevel = mineur.getLevel();

        if (blockType == Material.STONE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 12;
            } else if (mineurLevel <= 10) {
                xpGained = 9;
            } else if (mineurLevel <= 15) {
                xpGained = 6;
            } else if (mineurLevel <= 20) {
                xpGained = 3;
            } else if (mineurLevel <= 25) {
                xpGained = 2;
            } else {
                xpGained = 1;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.ANDESITE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 12;
            } else if (mineurLevel <= 10) {
                xpGained = 9;
            } else if (mineurLevel <= 15) {
                xpGained = 6;
            } else if (mineurLevel <= 20) {
                xpGained = 3;
            } else if (mineurLevel <= 25) {
                xpGained = 2;
            } else {
                xpGained = 1;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DIORITE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 12;
            } else if (mineurLevel <= 10) {
                xpGained = 9;
            } else if (mineurLevel <= 15) {
                xpGained = 6;
            } else if (mineurLevel <= 20) {
                xpGained = 3;
            } else if (mineurLevel <= 25) {
                xpGained = 2;
            } else {
                xpGained = 1;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.GRANITE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 12;
            } else if (mineurLevel <= 10) {
                xpGained = 9;
            } else if (mineurLevel <= 15) {
                xpGained = 6;
            } else if (mineurLevel <= 20) {
                xpGained = 3;
            } else if (mineurLevel <= 25) {
                xpGained = 2;
            } else {
                xpGained = 1;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.COAL_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 30;
            } else if (mineurLevel <= 10) {
                xpGained = 15;
            } else if (mineurLevel <= 15) {
                xpGained = 9;
            } else if (mineurLevel <= 20) {
                xpGained = 6;
            } else if (mineurLevel <= 25) {
                xpGained = 3;
            } else {
                xpGained = 1;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DEEPSLATE_COAL_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 30;
            } else if (mineurLevel <= 10) {
                xpGained = 15;
            } else if (mineurLevel <= 15) {
                xpGained = 9;
            } else if (mineurLevel <= 20) {
                xpGained = 6;
            } else if (mineurLevel <= 25) {
                xpGained = 3;
            } else {
                xpGained = 1;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.IRON_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 48;
            } else if (mineurLevel <= 10) {
                xpGained = 35;
            } else if (mineurLevel <= 15) {
                xpGained = 23;
            } else if (mineurLevel <= 20) {
                xpGained = 16;
            } else if (mineurLevel <= 25) {
                xpGained = 7;
            } else {
                xpGained = 1;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DEEPSLATE_IRON_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 48;
            } else if (mineurLevel <= 10) {
                xpGained = 35;
            } else if (mineurLevel <= 15) {
                xpGained = 23;
            } else if (mineurLevel <= 20) {
                xpGained = 16;
            } else if (mineurLevel <= 25) {
                xpGained = 7;
            } else {
                xpGained = 1;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.COPPER_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 18;
            } else if (mineurLevel <= 10) {
                xpGained = 15;
            } else if (mineurLevel <= 15) {
                xpGained = 12;
            } else if (mineurLevel <= 20) {
                xpGained = 9;
            } else if (mineurLevel <= 25) {
                xpGained = 6;
            } else {
                xpGained = 3;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DEEPSLATE_COPPER_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 18;
            } else if (mineurLevel <= 10) {
                xpGained = 15;
            } else if (mineurLevel <= 15) {
                xpGained = 12;
            } else if (mineurLevel <= 20) {
                xpGained = 9;
            } else if (mineurLevel <= 25) {
                xpGained = 6;
            } else {
                xpGained = 3;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.GOLD_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 38;
            } else if (mineurLevel <= 10) {
                xpGained = 27;
            } else if (mineurLevel <= 15) {
                xpGained = 20;
            } else if (mineurLevel <= 20) {
                xpGained = 12;
            } else if (mineurLevel <= 25) {
                xpGained = 6;
            } else {
                xpGained = 3;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DEEPSLATE_GOLD_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 38;
            } else if (mineurLevel <= 10) {
                xpGained = 27;
            } else if (mineurLevel <= 15) {
                xpGained = 20;
            } else if (mineurLevel <= 20) {
                xpGained = 12;
            } else if (mineurLevel <= 25) {
                xpGained = 6;
            } else {
                xpGained = 3;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.REDSTONE_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 30;
            } else if (mineurLevel <= 10) {
                xpGained = 25;
            } else if (mineurLevel <= 15) {
                xpGained = 20;
            } else if (mineurLevel <= 20) {
                xpGained = 15;
            } else if (mineurLevel <= 25) {
                xpGained = 10;
            } else {
                xpGained = 5;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DEEPSLATE_REDSTONE_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 30;
            } else if (mineurLevel <= 10) {
                xpGained = 25;
            } else if (mineurLevel <= 15) {
                xpGained = 20;
            } else if (mineurLevel <= 20) {
                xpGained = 15;
            } else if (mineurLevel <= 25) {
                xpGained = 10;
            } else {
                xpGained = 5;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.EMERALD_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 250;
            } else if (mineurLevel <= 10) {
                xpGained = 200;
            } else if (mineurLevel <= 15) {
                xpGained = 150;
            } else if (mineurLevel <= 20) {
                xpGained = 100;
            } else if (mineurLevel <= 25) {
                xpGained = 50;
            } else {
                xpGained = 40;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DEEPSLATE_EMERALD_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 250;
            } else if (mineurLevel <= 10) {
                xpGained = 200;
            } else if (mineurLevel <= 15) {
                xpGained = 150;
            } else if (mineurLevel <= 20) {
                xpGained = 100;
            } else if (mineurLevel <= 25) {
                xpGained = 50;
            } else {
                xpGained = 40;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.LAPIS_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 20;
            } else if (mineurLevel <= 10) {
                xpGained = 18;
            } else if (mineurLevel <= 15) {
                xpGained = 16;
            } else if (mineurLevel <= 20) {
                xpGained = 14;
            } else if (mineurLevel <= 25) {
                xpGained = 12;
            } else {
                xpGained = 10;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DEEPSLATE_LAPIS_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 20;
            } else if (mineurLevel <= 10) {
                xpGained = 18;
            } else if (mineurLevel <= 15) {
                xpGained = 16;
            } else if (mineurLevel <= 20) {
                xpGained = 14;
            } else if (mineurLevel <= 25) {
                xpGained = 12;
            } else {
                xpGained = 10;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DIAMOND_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 70;
            } else if (mineurLevel <= 10) {
                xpGained = 70;
            } else if (mineurLevel <= 15) {
                xpGained = 70;
            } else if (mineurLevel <= 20) {
                xpGained = 70;
            } else if (mineurLevel <= 25) {
                xpGained = 70;
            } else {
                xpGained = 70;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.DEEPSLATE_DIAMOND_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 70;
            } else if (mineurLevel <= 10) {
                xpGained = 70;
            } else if (mineurLevel <= 15) {
                xpGained = 70;
            } else if (mineurLevel <= 20) {
                xpGained = 70;
            } else if (mineurLevel <= 25) {
                xpGained = 70;
            } else {
                xpGained = 70;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.NETHER_GOLD_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 10;
            } else if (mineurLevel <= 10) {
                xpGained = 10;
            } else if (mineurLevel <= 15) {
                xpGained = 10;
            } else if (mineurLevel <= 20) {
                xpGained = 10;
            } else if (mineurLevel <= 25) {
                xpGained = 10;
            } else {
                xpGained = 10;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.NETHER_QUARTZ_ORE) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 20;
            } else if (mineurLevel <= 10) {
                xpGained = 20;
            } else if (mineurLevel <= 15) {
                xpGained = 15;
            } else if (mineurLevel <= 20) {
                xpGained = 15;
            } else if (mineurLevel <= 25) {
                xpGained = 10;
            } else {
                xpGained = 5;
            }

            mineur.ajouterExp(player, xpGained);
        }

        if (blockType == Material.ANCIENT_DEBRIS) {
            int xpGained;

            if (mineurLevel <= 5) {
                xpGained = 500;
            } else if (mineurLevel <= 10) {
                xpGained = 450;
            } else if (mineurLevel <= 15) {
                xpGained = 400;
            } else if (mineurLevel <= 20) {
                xpGained = 350;
            } else if (mineurLevel <= 25) {
                xpGained = 300;
            } else {
                xpGained = 250;
            }

            mineur.ajouterExp(player, xpGained);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        if (manager.getMineur(player).getLevel() >= 30){
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, -1, 0, false, false,true));
        }
    }
}