package fr.floppa.jobs.job.alchimiste;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AlchimisteRestrictionItems implements Listener {

    private final AlchimisteManager manager;

    public AlchimisteRestrictionItems(AlchimisteManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerUse(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;

            Alchimiste alchimiste = manager.getAlchimiste(player);

            if (item.getType() == Material.GUNPOWDER) {
                if (alchimiste.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Alchimiste pour utiliser : §dPoudre à canon §c!");
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.GLISTERING_MELON_SLICE) {
                if (alchimiste.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Alchimiste pour utiliser : §dTranche de pastèque scintillante §c!");
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.SPLASH_POTION) {
                if (alchimiste.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Alchimiste pour utiliser : §dPotion jetable §c!");
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.LINGERING_POTION) {
                if (alchimiste.getLevel() < 20) {
                    player.sendMessage("§cVous devez être niveau 20 en Alchimiste pour utiliser : §dPotion persistante §c!");
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.FERMENTED_SPIDER_EYE) {
                if (alchimiste.getLevel() < 25) {
                    player.sendMessage("§cVous devez être niveau 25 en Alchimiste pour utiliser : §Oeil d'araignée fermentée §c!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event){
        if (event.getEntity() instanceof Player player){
            ItemStack itemStack = event.getItem().getItemStack();

            Alchimiste alchimiste = manager.getAlchimiste(player);

            if (itemStack.getType() == Material.GUNPOWDER) {
                if (alchimiste.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Alchimiste pour utiliser : §dPoudre à canon §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.GLISTERING_MELON_SLICE) {
                if (alchimiste.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Alchimiste pour utiliser : §dTranche de pastèque scintillante §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.SPLASH_POTION) {
                if (alchimiste.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Alchimiste pour utiliser : §dPotion jetable §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.LINGERING_POTION) {
                if (alchimiste.getLevel() < 20) {
                    player.sendMessage("§cVous devez être niveau 20 en Alchimiste pour utiliser : §dPotion persistante §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.FERMENTED_SPIDER_EYE) {
                if (alchimiste.getLevel() < 25) {
                    player.sendMessage("§cVous devez être niveau 25 en Alchimiste pour utiliser : §Oeil d'araignée fermentée §c!");
                    event.setCancelled(true);
                }
            }
        }
    }
}
