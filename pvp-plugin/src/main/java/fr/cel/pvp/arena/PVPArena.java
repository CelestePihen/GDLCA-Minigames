package fr.cel.pvp.arena;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.pvp.manager.GameManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
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
    
    @Getter private final String arenaName;
    @Getter private final String displayName;

    private final Location spawnLoc;

    private final boolean fallDamage;
    private final boolean tridentActivated;

    @Getter private final List<UUID> players;

    public PVPArena(String arenaName, String displayName, Location spawnLoc, boolean fallDamage, boolean tridentActivated, GameManager gameManager) {
        this.arenaName = arenaName;
        this.displayName = displayName;
        this.spawnLoc = spawnLoc;

        this.fallDamage = fallDamage;
        this.tridentActivated = tridentActivated;

        this.players = new ArrayList<>();

        gameManager.getMain().getServer().getPluginManager().registerEvents(this, gameManager.getMain());
    }

    public void addPlayer(Player player) {
        if (isPlayerInArena(player)) return;

        GameAPI.getInstance().getPlayerManager().removePlayerInHub(player);
        players.add(player.getUniqueId());

        player.setRespawnLocation(spawnLoc, true);
        player.teleportAsync(spawnLoc);
        player.showTitle(Title.title(Component.text("PVP", NamedTextColor.GOLD), Component.text(displayName)));
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        giveWeapons(player);

        sendMessage(player.displayName().append(Component.text(" a rejoint l'arène !")));
    }

    public void removePlayer(Player player) {
        if (!players.contains(player.getUniqueId())) return;
        getPlayers().remove(player.getUniqueId());
    }

    public boolean isPlayerInArena(Player player) {
        return players.contains(player.getUniqueId());
    }

    private void sendMessage(Component message) {
        message = GameManager.getPrefix().append(message);
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.sendMessage(message);
        }
    }

    public void giveWeapons(Player player) {
        ItemStack diamondSword = new ItemBuilder(Material.DIAMOND_SWORD).itemName(Component.text("Lame sacrée de Ludwig")).setUnbreakable().toItemStack();
        ItemStack bow = new ItemBuilder(Material.BOW).addEnchant(Enchantment.INFINITY, 1).setUnbreakable().toItemStack();
        ItemStack arrow = new ItemBuilder(Material.ARROW).toItemStack();

        player.getInventory().setItem(17, arrow);
        player.getInventory().addItem(diamondSword, bow);

        if (tridentActivated) {
            ItemStack trident = new ItemBuilder(Material.TRIDENT).itemName(Component.text("Trident de Poséidon"))
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
        if (isPlayerInArena(player) && event.getMessage().contains("/hub")) removePlayer(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player damager = player.getKiller();

        if (damager == null || !isPlayerInArena(player) || !isPlayerInArena(damager)) return;

        // Donne une pomme dorée au tueur pour se régénérer
        damager.getInventory().addItem(ItemStack.of(Material.GOLDEN_APPLE));
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