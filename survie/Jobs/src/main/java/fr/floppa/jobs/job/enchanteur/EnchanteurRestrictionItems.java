package fr.floppa.jobs.job.enchanteur;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EnchanteurRestrictionItems implements Listener {

    private final EnchanteurManager manager;

    public EnchanteurRestrictionItems(EnchanteurManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerUse(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;

            Enchanteur enchanteur = manager.getEnchanteur(player);

            if (itemStack.getType() == Material.BOOKSHELF) {
                if (enchanteur.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Enchanteur pour utiliser : §dBibliothèque §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.LECTERN) {
                if (enchanteur.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Enchanteur pour utiliser : §dPupitre §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.EXPERIENCE_BOTTLE) {
                if (enchanteur.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Enchanteur pour utiliser : §dFiole d'expérience §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                if (itemStack.containsEnchantment(Enchantment.UNBREAKING)) {
                    if (enchanteur.getLevel() < 20) {
                        player.sendMessage("§cVous devez être niveau 20 en Enchanteur pour utiliser : §dSolidité §c!");
                        event.setCancelled(true);
                    }
                }

            }

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                if (itemStack.containsEnchantment(Enchantment.MENDING)) {
                    if (enchanteur.getLevel() < 25) {
                        player.sendMessage("§cVous devez être niveau 25 en Enchanteur pour utiliser : §dRaccommodage §c!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack itemStack = event.getItem().getItemStack();

            Enchanteur enchanteur = manager.getEnchanteur(player);

            if (itemStack.getType() == Material.BOOKSHELF) {
                if (enchanteur.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Enchanteur pour utiliser : §dBibliothèque §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.LECTERN) {
                if (enchanteur.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Enchanteur pour utiliser : §dPupitre §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.EXPERIENCE_BOTTLE) {
                if (enchanteur.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Enchanteur pour utiliser : §dFiole d'expérience §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                if (itemStack.containsEnchantment(Enchantment.UNBREAKING)) {
                    if (enchanteur.getLevel() < 20) {
                        player.sendMessage("§cVous devez être niveau 20 en Enchanteur pour utiliser : §dSolidité §c!");
                        event.setCancelled(true);
                    }
                }
            }

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                if (itemStack.containsEnchantment(Enchantment.MENDING)) {
                    if (enchanteur.getLevel() < 25) {
                        player.sendMessage("§cVous devez être niveau 25 en Enchanteur pour utiliser : §dRaccommodage §c!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player player) {

            ItemStack item = event.getCurrentItem();
            if (item == null) return;

            if (item.getType() == Material.BOOKSHELF) {
                if (manager.getEnchanteur(player).getLevel() < 5) {
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.LECTERN) {
                if (manager.getEnchanteur(player).getLevel() < 10) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
