package fr.cel.pvp.manager.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.pvp.manager.PVPGameManager;
import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;

public class PVPArena implements Listener {
    
    public PVPGameManager gameManager = PVPGameManager.getGameManager();

    @Getter private final String nameArena;
    @Getter private final String displayName;

    @Getter private final Location spawnLoc;

    @Getter private final List<UUID> players;

    public PVPArena(String nameArena, String displayName, Location spawnLoc) {
        this.nameArena = nameArena;
        this.displayName = displayName;

        this.spawnLoc = spawnLoc;

        this.players = new ArrayList<>();

        gameManager.getMain().getServer().getPluginManager().registerEvents(this, gameManager.getMain());
    }

    public void addPlayer(Player player) {
        if (players.contains(player.getUniqueId())) return;

        Hub.getHub().getPlayerManager().removePlayerInHub(player);
        players.add(player.getUniqueId());
        sendMessage(player.getDisplayName() + " a rejoint l'arène !");

        player.setBedSpawnLocation(spawnLoc, true);
        player.teleport(this.getSpawnLoc());
        player.sendTitle(ChatUtility.format("&6PVP"), this.getDisplayName(), 10, 70, 20);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        this.giveWeapons(player);
    }

    public void removePlayer(Player player) {
        if (!players.contains(player.getUniqueId())) return;
        this.getPlayers().remove(player.getUniqueId());
        gameManager.getPlayerManager().sendPlayerToHub(player);
    }

    public boolean isPlayerInArena(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void sendMessage(String message) {
        message = ChatUtility.format(gameManager.getPrefix() + message);
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.sendMessage(message);
        }
    }

    public void giveWeapons(Player player) {
        ItemStack diamond_sword = new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("La Lame Sacrée de Ludwig").toItemStack();
        ItemStack bow = new ItemBuilder(Material.BOW).setDisplayName("Arc Long").addEnchant(Enchantment.ARROW_INFINITE, 1).toItemStack();
        ItemStack arrow = new ItemBuilder(Material.ARROW).toItemStack();
        ItemStack golden_carrot = new ItemBuilder(Material.GOLDEN_CARROT, 64).toItemStack();
        player.getInventory().addItem(diamond_sword, bow, arrow, golden_carrot);
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    }

    @EventHandler
    public void quit(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equals("/hub") || event.getMessage().equals("/hub:hub")) {
            if (!this.isPlayerInArena(player)) return;
            this.removePlayer(player);
        }
    }

}