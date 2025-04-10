package fr.floppa.jobs.job.explorateur;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ExplorateurRestrictionItems implements Listener {

    private final ExplorateurManager manager;

    public ExplorateurRestrictionItems(ExplorateurManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerUse(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;

            Explorateur explorateur = manager.getExplorateur(player);

            if (itemStack.getType().name().contains("_BED")) {
                if (explorateur.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Explorateur pour utiliser : §dLit §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.DRAGON_EGG) {
                if (explorateur.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Explorateur pour utiliser : §dOeuf de dragon §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                if (itemStack.containsEnchantment(Enchantment.FROST_WALKER)) {
                    if (explorateur.getLevel() < 20) {
                        player.sendMessage("§cVous devez être niveau 20 en Explorateur pour utiliser : §Semelles givrantes §c!");
                        event.setCancelled(true);
                    }
                }
            }

            if (itemStack.getType() == Material.SHULKER_BOX) {
                if (explorateur.getLevel() < 25) {
                    player.sendMessage("§cVous devez être niveau 25 en Explorateur pour utiliser : §dBoîte de Shulker §c!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack itemStack = event.getItem().getItemStack();

            Explorateur explorateur = manager.getExplorateur(player);

            if (itemStack.getType().name().contains("_BED")) {
                if (explorateur.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Explorateur pour utiliser : §dLit §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.DRAGON_EGG) {
                if (explorateur.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Explorateur pour utiliser : §dOeuf de dragon §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                if (itemStack.containsEnchantment(Enchantment.FROST_WALKER)) {
                    if (explorateur.getLevel() < 20) {
                        player.sendMessage("§cVous devez être niveau 20 en Explorateur pour utiliser : §Semelles givrantes §c!");
                        event.setCancelled(true);
                    }
                }
            }

            if (itemStack.getType() == Material.SHULKER_BOX) {
                if (explorateur.getLevel() < 25) {
                    player.sendMessage("§cVous devez être niveau 25 en Explorateur pour utiliser : §dBoîte de Shulker §c!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if (event.getWhoClicked() instanceof Player player){

            ItemStack item = event.getCurrentItem();
            if (item == null) return;

            if (item.getType().name().contains("_BED")){
                if (manager.getExplorateur(player).getLevel() < 5){
                    event.setCancelled(true);
                }
            }

            if (event.getCurrentItem().getType() == Material.SHULKER_BOX) {
                if (manager.getExplorateur(player).getLevel() < 25){
                    event.setCancelled(true);
                }
            }
        }
    }

}