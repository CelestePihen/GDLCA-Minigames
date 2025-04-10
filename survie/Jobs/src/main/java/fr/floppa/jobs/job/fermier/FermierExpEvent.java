package fr.floppa.jobs.job.fermier;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FermierExpEvent implements Listener {

    private final FermierManager manager;

    public FermierExpEvent(FermierManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlantEvent(PlayerInteractEvent event) {
        // Vérifier que l'action est un clic droit sur un bloc
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Vérifier que la main utilisée est la main principale (éviter double déclenchement)
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // Vérifier si le joueur clique sur de la terre cultivée
        if (block == null || block.getType() != Material.FARMLAND) {
            return;
        }

        // Vérifier si l'objet tenu est une graine
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        Material itemType = itemInHand.getType();

        int xpGained;

        switch (itemType) {
            case BEETROOT_SEEDS:
            case MELON_SEEDS:
            case PUMPKIN_SEEDS:
            case TORCHFLOWER_SEEDS:
            case WHEAT_SEEDS:
            case CARROT:
            case POTATO:
                xpGained = 2;
                break;
            default:
                return;
        }

        // Ajouter l'XP au fermier
        Fermier fermier = manager.getFermier(player);
        fermier.ajouterExp(player, xpGained);
    }

    @EventHandler
    public void onBreakFarmer(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (blockType == Material.WHEAT) {
            if (block.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) { // Stade maximal atteint
                    Fermier fermier = manager.getFermier(player);

                    int fermierLevel = fermier.getLevel();
                    int xpGained;

                    if (fermierLevel <= 5) {
                        xpGained = 75;
                    } else if (fermierLevel <= 10) {
                        xpGained = 70;
                    } else if (fermierLevel <= 15) {
                        xpGained = 65;
                    } else if (fermierLevel <= 20) {
                        xpGained = 60;
                    } else if (fermierLevel <= 25) {
                        xpGained = 55;
                    } else {
                        xpGained = 50;
                    }
                    
                    fermier.ajouterExp(player, xpGained);
                }
            }
        }

        if (blockType == Material.MELON) {
            Fermier fermier = manager.getFermier(player);
            int fermierLevel = fermier.getLevel();
            int xpGained;

            if (fermierLevel <= 5) {
                xpGained = 150;
            } else if (fermierLevel <= 10) {
                xpGained = 125;
            } else if (fermierLevel <= 15) {
                xpGained = 100;
            } else if (fermierLevel <= 20) {
                xpGained = 60;
            } else if (fermierLevel <= 25) {
                xpGained = 45;
            } else {
                xpGained = 40;
            }

            fermier.ajouterExp(player, xpGained);
        }

        if (blockType == Material.CARROT) {
            if (block.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) { // Stade maximal atteint
                    Fermier fermier = manager.getFermier(player);
                    int fermierLevel = fermier.getLevel();
                    int xpGained;

                    if (fermierLevel <= 5) {
                        xpGained = 75;
                    } else if (fermierLevel <= 10) {
                        xpGained = 70;
                    } else if (fermierLevel <= 15) {
                        xpGained = 65;
                    } else if (fermierLevel <= 20) {
                        xpGained = 60;
                    } else if (fermierLevel <= 25) {
                        xpGained = 55;
                    } else {
                        xpGained = 50;
                    }

                    fermier.ajouterExp(player, xpGained);
                }
            }
        }

        if (blockType == Material.PUMPKIN) {
            Fermier fermier = manager.getFermier(player);
            int fermierLevel = fermier.getLevel();
            int xpGained;

            if (fermierLevel <= 5) {
                xpGained = 150;
            } else if (fermierLevel <= 10) {
                xpGained = 125;
            } else if (fermierLevel <= 15) {
                xpGained = 100;
            } else if (fermierLevel <= 20) {
                xpGained = 60;
            } else if (fermierLevel <= 25) {
                xpGained = 45;
            } else {
                xpGained = 40;
            }

            fermier.ajouterExp(player, xpGained);
        }

        if (blockType == Material.POTATO) {
            if (block.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) { // Stade maximal atteint
                    Fermier fermier = manager.getFermier(player);
                    int fermierLevel = fermier.getLevel();
                    int xpGained;

                    if (fermierLevel <= 5) {
                        xpGained = 75;
                    } else if (fermierLevel <= 10) {
                        xpGained = 70;
                    } else if (fermierLevel <= 15) {
                        xpGained = 65;
                    } else if (fermierLevel <= 20) {
                        xpGained = 60;
                    } else if (fermierLevel <= 25) {
                        xpGained = 55;
                    } else {
                        xpGained = 50;
                    }

                    fermier.ajouterExp(player, xpGained);
                }
            }
        }

        if (blockType == Material.BEETROOTS) {
            if (block.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) { // Stade maximal atteint
                    Fermier fermier = manager.getFermier(player);
                    int fermierLevel = fermier.getLevel();
                    int xpGained;

                    if (fermierLevel <= 5) {
                        xpGained = 75;
                    } else if (fermierLevel <= 10) {
                        xpGained = 70;
                    } else if (fermierLevel <= 15) {
                        xpGained = 65;
                    } else if (fermierLevel <= 20) {
                        xpGained = 60;
                    } else if (fermierLevel <= 25) {
                        xpGained = 55;
                    } else {
                        xpGained = 50;
                    }

                    fermier.ajouterExp(player, xpGained);
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        if (manager.getFermier(player).getLevel() >= 30){
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, -1, 1, false, false,true));
        }
    }

}