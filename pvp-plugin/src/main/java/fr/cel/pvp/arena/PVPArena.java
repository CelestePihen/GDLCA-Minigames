package fr.cel.pvp.arena;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.pvp.manager.GameManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PVPArena implements Listener {
    
    private final GameManager gameManager;

    private final String nameArena;
    @Getter private final String displayName;

    private final Location spawnLoc;

    private final boolean fallDamage;
    private final boolean tridentActivated;

    @Getter private final List<UUID> players;

    public PVPArena(String nameArena, String displayName, Location spawnLoc, boolean fallDamage, boolean tridentActivated, GameManager gameManager) {
        this.nameArena = nameArena;
        this.displayName = displayName;
        this.spawnLoc = spawnLoc;

        this.fallDamage = fallDamage;
        this.tridentActivated = tridentActivated;

        this.players = new ArrayList<>();

        this.gameManager = gameManager;
        gameManager.getMain().getServer().getPluginManager().registerEvents(this, gameManager.getMain());
    }

    public void addPlayer(Player player) {
        if (isPlayerInArena(player)) return;

        GameAPI.getInstance().getPlayerManager().removePlayerInHub(player);
        players.add(player.getUniqueId());

        player.setRespawnLocation(spawnLoc, true);
        player.teleport(spawnLoc);
        player.sendTitle(ChatUtility.format("&6PVP"), displayName, 10, 70, 20);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        giveWeapons(player);

        sendMessage(player.getDisplayName() + " a rejoint l'arène !");
    }

    public void removePlayer(Player player) {
        if (!players.contains(player.getUniqueId())) return;
        getPlayers().remove(player.getUniqueId());
    }

    public boolean isPlayerInArena(Player player) {
        return players.contains(player.getUniqueId());
    }

    private void sendMessage(String message) {
        message = gameManager.getPrefix() + ChatUtility.format(message);
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.sendMessage(message);
        }
    }

    public void giveWeapons(Player player) {
        ItemStack diamond_sword = new ItemBuilder(Material.DIAMOND_SWORD).setItemName("Lame sacrée de Ludwig").setUnbreakable().toItemStack();
        ItemStack bow = new ItemBuilder(Material.BOW).addEnchant(Enchantment.INFINITY, 1).setUnbreakable().toItemStack();
        ItemStack arrow = new ItemBuilder(Material.ARROW).toItemStack();

        player.getInventory().setItem(17, arrow);
        player.getInventory().addItem(diamond_sword, bow);

        if (tridentActivated) {
            ItemStack trident = new ItemBuilder(Material.TRIDENT).setItemName("Trident de Poséidon")
                    .addEnchant(Enchantment.RIPTIDE, 3).setUnbreakable().toItemStack();
            player.getInventory().addItem(trident);
        }

        player.getInventory().setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).setUnbreakable().toItemStack());
        player.getInventory().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).setUnbreakable().toItemStack());
        player.getInventory().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).setUnbreakable().toItemStack());
        player.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).setUnbreakable().toItemStack());
        player.getInventory().setItemInOffHand(new ItemBuilder(Material.SHIELD).setUnbreakable().toItemStack());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!isPlayerInArena(player) || !event.getMessage().contains("/hub")) return;
        removePlayer(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player damager = player.getKiller();

        if (damager == null || !isPlayerInArena(player) || !isPlayerInArena(damager)) return;

        // Donne une pomme dorée au tueur pour se régénérer
        damager.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !isPlayerInArena(player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(!fallDamage);
    }

    @EventHandler
    public void onFoodChanged(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && !isPlayerInArena(player)) event.setCancelled(true);
    }

}