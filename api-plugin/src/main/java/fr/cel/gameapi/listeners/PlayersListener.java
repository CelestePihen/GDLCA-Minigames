package fr.cel.gameapi.listeners;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PlayersListener implements Listener {

    private final GameAPI main;

    public PlayersListener(GameAPI main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            event.setJoinMessage(GameAPI.getPrefix() + "Bienvenue à " + player.getName() + " sur le serveur !");

            main.getPlayerManager().setNewPlayer(player.getUniqueId());
            main.getPlayerManager().getPlayersWhoWelcomed().clear();

            main.getDatabase().createAccount(player);
        } else {
            event.setJoinMessage(ChatUtility.format("[&a+&r] ") + player.getName());
        }

        main.getPlayerManager().addPlayerData(player);
        main.getPlayerManager().sendPlayerToHub(player);

        player.setPlayerListHeader(ChatUtility.format("Amusez-vous bien sur GDLCA Minigames !", ChatUtility.GREEN));
        player.setPlayerListFooter(ChatUtility.format("Discord : &fdiscord.gg/vFjPYC4Mj8", ChatUtility.AQUA));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        event.setQuitMessage(ChatUtility.format("[&c-&r] ") + player.getName());
        main.getPlayerManager().removePlayerInHub(player);
        main.getPlayerManager().removePlayerData(player);
    }

    // Remove commands from no-op players
    private final Set<String> blockedCommands = new HashSet<>(Arrays.asList(
            "?", "about", "help", "pl", "plugins", "ver", "version",
            "bukkit:?", "bukkit:about", "bukkit:help", "bukkit:pl", "bukkit:plugins", "bukkit:ver", "bukkit:version",
            "callback", "paper:callback",
            "me", "trigger",
            "minecraft:me", "minecraft:trigger", "minecraft:help",
            "fastasyncworldedit", "fawe", "we", "worldedit",
            "fastasyncworldedit:fastasyncworldedit", "fastasyncworldedit:fawe", "fastasyncworldedit:we", "fastasyncworldedit:worldedit"
    ));

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().isOp()) return;

        for (String cmd : blockedCommands) {
            if (event.getMessage().toLowerCase().startsWith("/" + cmd))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        if (event.getPlayer().isOp()) return;
        event.getCommands().removeAll(blockedCommands);
    }

}