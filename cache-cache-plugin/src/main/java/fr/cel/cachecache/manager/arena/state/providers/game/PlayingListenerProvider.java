package fr.cel.cachecache.manager.arena.state.providers.game;

import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.cachecache.manager.items.SoundCatTimer;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlayingListenerProvider extends StateListenerProvider {

    private SoundCatTimer soundCatTimer;

    private List<Sound> goatHornSounds = Arrays.asList(Sound.ITEM_GOAT_HORN_SOUND_4, Sound.ITEM_GOAT_HORN_SOUND_7);

    public PlayingListenerProvider(CCArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onDeathAndKill(PlayerDeathEvent event) {

        Player victim = event.getEntity();
        if (!getArena().isPlayerInArena(victim)) return;

        if (getArena().getTimer() < 30) {
            event.setDeathMessage("");
            getArena().sendMessage("Le joueur " + victim.getName() + " est mort avant les 30 secondes d'attente. Il est donc ressucité.");
        }

        else {
            event.setDeathMessage("");
            getArena().sendMessage(victim.getName() + " n'est pas mort par le tueur. Il est donc ressucité.");
        }
    }

    @EventHandler
    public void playerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItem();

        if (getArena().getSpawnedGroundItems().contains(item)) {
            getArena().getSpawnedGroundItems().remove(item);
            player.sendMessage(getArena().getGameManager().getPrefix() + "Vous avez récupéré " + item.getItemStack().getItemMeta().getDisplayName());
        }

    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player pl && entity instanceof Player p) {
            if (!getArena().isPlayerInArena(pl)) return;
            if (!getArena().isPlayerInArena(p)) return;

            if (getArena().getHunterMode() == CCArena.HunterMode.LoupToucheTouche && getArena().getHiders().contains(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);
            getArena().eliminate(p);
        }

        if (damager instanceof Player && !(entity instanceof Player) || entity instanceof ArmorStand) {
            if (!getArena().isPlayerInArena((Player) damager)) return;
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (!getArena().isPlayerInArena(player)) return;

            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;
            if (itemStack.getItemMeta() == null) return;
            String name = itemStack.getItemMeta().getDisplayName();

            if (event.getView().getTitle().equalsIgnoreCase("Joueurs")) {
                Player target = Bukkit.getPlayer(name);
                if (getArena().getPlayers().contains(target.getUniqueId())) {
                    Location tempLocation = player.getLocation();

                    player.teleport(target.getLocation());
                    target.teleport(tempLocation);

                    removeItem(player);
                } else {
                    player.sendMessage(getArena().getGameManager().getPrefix() + "Ce joueur n'est pas disponible. Merci de réouvrir le menu.");
                }
                player.closeInventory();
            }

            if (event.getView().getTitle().equalsIgnoreCase("Sons")) {
                switch (itemStack.getType()) {
                    case STRING -> {
                        soundCatTimer = new SoundCatTimer(getArena());
                        soundCatTimer.runTaskTimer(CCGameManager.getGameManager().getMain(), 0, 20);

                        player.closeInventory();
                        removeItem(player);
                    }

                    case GOAT_HORN -> {
                        Sound sound = goatHornSounds.get(new Random().nextInt(goatHornSounds.size()));
                        getArena().getPlayers().forEach(uuid -> {
                            Player pl = Bukkit.getPlayer(uuid);
                            if (pl.getGameMode() == GameMode.SPECTATOR) return;
                            pl.playSound(pl, sound, SoundCategory.AMBIENT, 2.0f, 1.0f);

                            player.closeInventory();
                            removeItem(player);
                        });
                    }

                    default -> { }
                }

            }

        }
    }

    private void removeItem(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() == 1) player.getInventory().setItemInMainHand(null);
        else itemInHand.setAmount(itemInHand.getAmount() - 1);
    }
    
}