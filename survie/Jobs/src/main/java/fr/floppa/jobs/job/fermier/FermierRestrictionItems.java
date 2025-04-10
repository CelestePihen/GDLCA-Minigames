package fr.floppa.jobs.job.fermier;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class FermierRestrictionItems implements Listener {

    private final FermierManager manager;

    public FermierRestrictionItems(FermierManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerUse(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;

            Fermier fermier = manager.getFermier(player);

            if (itemStack.getType() == Material.IRON_HOE || itemStack.getType() == Material.DIAMOND_HOE || itemStack.getType() == Material.NETHERITE_HOE) {
                if (fermier.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Fermier pour utiliser : §dHoue en fer/diamant/netherite §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.BONE_MEAL) {
                if (fermier.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Fermier pour utiliser : §dPoudre d'os §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.GOLDEN_APPLE) {
                if (fermier.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Fermier pour utiliser : §dPomme dorée §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.GOLDEN_CARROT) {
                if (fermier.getLevel() < 20) {
                    player.sendMessage("§cVous devez être niveau 15 en Fermier pour utiliser : §dCarotte dorée §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                if (itemStack.containsEnchantment(Enchantment.FORTUNE)) {
                    if (fermier.getLevel() < 25) {
                        player.sendMessage("§cVous devez être niveau 25 en Fermier pour utiliser : §Fortune §c!");
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

            Fermier fermier = manager.getFermier(player);

            if (itemStack.getType() == Material.IRON_HOE || itemStack.getType() == Material.DIAMOND_HOE || itemStack.getType() == Material.NETHERITE_HOE) {
                if (fermier.getLevel() < 5) {
                    player.sendMessage("§cVous devez être niveau 5 en Fermier pour utiliser : §dHoue en fer/diamant/netherite §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.BONE_MEAL) {
                if (fermier.getLevel() < 10) {
                    player.sendMessage("§cVous devez être niveau 10 en Fermier pour utiliser : §dPoudre d'os §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.GOLDEN_APPLE) {
                if (fermier.getLevel() < 15) {
                    player.sendMessage("§cVous devez être niveau 15 en Fermier pour utiliser : §dPomme dorée §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.GOLDEN_CARROT) {
                if (fermier.getLevel() < 20) {
                    player.sendMessage("§cVous devez être niveau 15 en Fermier pour utiliser : §dCarotte dorée §c!");
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                if (itemStack.containsEnchantment(Enchantment.FORTUNE)) {
                    if (fermier.getLevel() < 25) {
                        player.sendMessage("§cVous devez être niveau 25 en Fermier pour utiliser : §Fortune §c!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;

            if (itemStack.getType() == Material.IRON_HOE || itemStack.getType() == Material.DIAMOND_HOE || itemStack.getType() == Material.NETHERITE_HOE) {
                if (manager.getFermier(player).getLevel() < 5) {
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.BONE_MEAL) {
                if (manager.getFermier(player).getLevel() < 10) {
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.GOLDEN_APPLE) {
                if (manager.getFermier(player).getLevel() < 15) {
                    event.setCancelled(true);
                }
            }

            if (itemStack.getType() == Material.GOLDEN_CARROT) {
                if (manager.getFermier(player).getLevel() < 20) {
                    event.setCancelled(true);
                }
            }
        }
    }

}