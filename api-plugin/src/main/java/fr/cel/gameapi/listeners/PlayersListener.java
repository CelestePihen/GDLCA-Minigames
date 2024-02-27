package fr.cel.gameapi.listeners;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayersListener implements Listener {

    private final GameAPI main;

    @Getter private final Set<UUID> newPlayers = new HashSet<>();
    @Getter private final Set<UUID> players = new HashSet<>();

    public PlayersListener(GameAPI main) {
        this.main = main;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            event.setJoinMessage(GameAPI.getInstance().getPrefix() + "Bienvenue à " + player.getName() + " sur le serveur !");
        } else {
            event.setJoinMessage("");
        }

        main.getPlayerManager().sendPlayerToSpawn(player);
        player.setPlayerListHeader(ChatUtility.format("Bienvenue sur &9GDLCA Minigames&f !"));
        player.setPlayerListFooter(ChatUtility.format(ChatUtility.UtilityColor.AQUA.getCharacter() + "Discord : &fdiscord.gg/vFjPYC4Mj8"));

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, -1, 255, false, false, false));

        UUID uuid = player.getUniqueId();
        if (!main.getDatabase().hasAccount(player)) {
            newPlayers.add(uuid);
            player.sendMessage(GameAPI.getInstance().getPrefix() + "Merci de faire la commande : /register <mot de passe> <mot de passe>");
        } else {
            players.add(uuid);
            main.getPlayerManager().addPlayerData(player);
            player.sendMessage(GameAPI.getInstance().getPrefix() + "Merci de faire la commande : /login <mot de passe>");
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (!newPlayers.contains(player.getUniqueId())) {
            event.setQuitMessage(ChatUtility.format("[&c-&r] ") + player.getName());
        }

        newPlayers.remove(player.getUniqueId());
        players.remove(player.getUniqueId());

        GameAPI.getInstance().getPlayerManager().removePlayerInHub(player);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (newPlayers.contains(player.getUniqueId()) || players.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (newPlayers.contains(uuid)) {
            player.sendMessage(GameAPI.getInstance().getPrefix() + "Merci de faire la commande : /register <mot de passe> <mot de passe>");
            event.setCancelled(true);
            return;
        }

        if (players.contains(uuid)) {
            player.sendMessage(GameAPI.getInstance().getPrefix() + "Merci de faire la commande : /login <mot de passe>");
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (event.getMessage().contains("/register") || event.getMessage().contains("/login")) return;

        if (newPlayers.contains(uuid)) {
            player.sendMessage(GameAPI.getInstance().getPrefix() + "Merci de faire la commande : /register <mot de passe> <mot de passe>");
            event.setCancelled(true);
            return;
        }

        if (players.contains(uuid)) {
            player.sendMessage(GameAPI.getInstance().getPrefix() + "Merci de faire la commande : /login <mot de passe>");
            event.setCancelled(true);
        }
    }

}