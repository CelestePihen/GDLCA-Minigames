package fr.floppa.jobs.job.mineur;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MineurRestrictionItems implements Listener {

    private final MineurManager manager;

    public MineurRestrictionItems(MineurManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerUse(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;

            Mineur mineur = manager.getMineur(player);

            if (itemStack.getType().name().contains("COPPER")) {
                if (mineur.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Mineur pour utiliser : §dCuivre §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType().name().contains("IRON")) {
                if (mineur.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Mineur pour utiliser : §dFer §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType().name().contains("DIAMOND")) {
                if (mineur.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Mineur pour utiliser : §dDiamant §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType().name().contains("NETHERITE")) {
                if (mineur.getLevel() < 20) {
                    player.sendMessage("§cVous devez être niveau 20 en Mineur pour utiliser : §dNetherite §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.MACE) {
                if (mineur.getLevel() < 25) {
                    player.sendMessage("§cVous devez être niveau 25 en Mineur pour utiliser : §dMasse §c!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack itemStack = event.getItem().getItemStack();

            Mineur mineur = manager.getMineur(player);

            if (itemStack.getType().name().contains("COPPER")) {
                if (mineur.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Mineur pour utiliser : §dCuivre §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType().name().contains("IRON")) {
                if (mineur.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Mineur pour utiliser : §dFer §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType().name().contains("DIAMOND")) {
                if (mineur.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Mineur pour utiliser : §dDiamant §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType().name().contains("NETHERITE")) {
                if (mineur.getLevel() < 20) {
                    player.sendMessage("§cVous devez être niveau 20 en Mineur pour utiliser : §dNetherite §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.MACE) {
                if (mineur.getLevel() < 25) {
                    player.sendMessage("§cVous devez être niveau 25 en Mineur pour utiliser : §dMasse §c!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if (event.getWhoClicked() instanceof Player player) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;

            if (itemStack.getType().name().contains("COPPER")) {
                if (manager.getMineur(player).getLevel() < 5) {
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType().name().contains("IRON")) {
                if (manager.getMineur(player).getLevel() < 10) {
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType().name().contains("DIAMOND")) {
                if (manager.getMineur(player).getLevel() < 15) {
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType().name().contains("NETHERITE")) {
                if (manager.getMineur(player).getLevel() < 20) {
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.MACE) {
                if (manager.getMineur(player).getLevel() < 25) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
