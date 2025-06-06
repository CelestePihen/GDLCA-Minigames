package fr.cel.gameapi.listeners;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.AdvancementsManager;
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

public final class PlayersListener implements Listener {

    private final GameAPI main;

    // The commands to remove from no-op players
    private final Set<String> blockedCommands = new HashSet<>(Arrays.asList(
            "?", "about", "help", "pl", "plugins", "ver", "version",
            "bukkit:?", "bukkit:about", "bukkit:help", "bukkit:pl", "bukkit:plugins", "bukkit:ver", "bukkit:version",
            "callback", "paper:callback",
            "me", "trigger",
            "minecraft:me", "minecraft:trigger", "minecraft:help",
            "fastasyncworldedit", "fawe", "we", "worldedit",
            "fastasyncworldedit:fastasyncworldedit", "fastasyncworldedit:fawe", "fastasyncworldedit:we", "fastasyncworldedit:worldedit"
    ));

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
            main.getAdvancementsManager().giveAdvancement(player, AdvancementsManager.Advancements.ROOT);
        } else {
            event.setJoinMessage(ChatUtility.format("[&a+&r] ") + player.getName());
        }

        main.getPlayerManager().addPlayerData(player);
        main.getPlayerManager().sendPlayerToHub(player);

        player.setPlayerListHeader(ChatUtility.format("&aAmusez-vous bien sur &bGDLCA Minigames !"));
        player.setPlayerListFooter(ChatUtility.format("&3Discord &f: discord.gg/vFjPYC4Mj8"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        event.setQuitMessage(ChatUtility.format("[&c-&r] ") + player.getName());
        main.getPlayerManager().removePlayerInHub(player);
        main.getPlayerManager().removePlayerData(player);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().isOp()) return;

        for (String cmd : blockedCommands) {
            if (event.getMessage().toLowerCase().startsWith("/" + cmd)) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        if (event.getPlayer().isOp()) return;
        event.getCommands().removeAll(blockedCommands);
    }

}