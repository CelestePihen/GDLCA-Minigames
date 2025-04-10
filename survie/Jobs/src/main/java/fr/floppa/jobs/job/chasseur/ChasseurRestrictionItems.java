package fr.floppa.jobs.job.chasseur;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChasseurRestrictionItems implements Listener {

    private final ChasseurManager manager;

    public ChasseurRestrictionItems(ChasseurManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerUse(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            ItemStack item = event.getCurrentItem();

            if (item == null) return;

            Chasseur chasseur = manager.getChasseur(player);

            if (item.getType() == Material.SHIELD) {
                if (chasseur.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Chasseur pour utiliser : §dBouclier §c!");
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.BOW) {
                if (chasseur.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Chasseur pour utiliser : §dArc §c!");
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.ENDER_PEARL) {
                if (chasseur.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Chasseur pour utiliser : §dPerle de l'ender §c!");
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.TRIDENT) {
                if (chasseur.getLevel() < 20) {
                    player.sendMessage("§cVous devez être niveau 20 en Chasseur pour utiliser : §dTrident §c!");
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.ELYTRA) {
                if (chasseur.getLevel() < 25) {
                    player.sendMessage("§cVous devez être niveau 25 en Chasseur pour utiliser : §dÉlytres §c!");
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onPickUp(EntityPickupItemEvent event){
        if (event.getEntity() instanceof Player player) {
            Item item = event.getItem();
            ItemStack itemStack = item.getItemStack();

            Chasseur chasseur = manager.getChasseur(player);

            if (itemStack.getType() == Material.SHIELD) {
                if (chasseur.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Chasseur pour utiliser : §dBouclier §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.BOW) {
                if (chasseur.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Chasseur pour utiliser : §dArc §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.ENDER_PEARL) {
                if (chasseur.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Chasseur pour utiliser : §dPerle de l'Ender §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.TRIDENT) {
                if (chasseur.getLevel() < 20) {
                    player.sendMessage("§cVous devez être niveau 20 en Chasseur pour utiliser : §dTrident §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.ELYTRA) {
                if (chasseur.getLevel() < 25) {
                    player.sendMessage("§cVous devez être niveau 25 en Chasseur pour utiliser : §dÉlytres §c!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if (event.getWhoClicked() instanceof Player player) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;

            if (item.getType() == Material.BOW && manager.getChasseur(player).getLevel() < 10) {
                event.setCancelled(true);
            }
        }
    }

}