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

import java.util.HashSet;
import java.util.Set;

public class PlayersListener implements Listener {

    private final GameAPI main;

    private final Set<String> blockedCommands = Set.of("we", "br", "brush", "none", "toggleplace", "tool", ";", "ver", "version", "about", "pl", "plugin", "me", "trigger", "help");
    private final Set<String> blockedCommands2 = Set.of("/we", "/br", "/brush", "/none", "/toggleplace", "/tool", "/;", "/ver", "/version", "/about", "/pl", "/plugin", "/me", "/trigger", "/help");

    public PlayersListener(GameAPI main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            event.setJoinMessage(GameAPI.getPrefix() + "Bienvenue à " + player.getName() + " sur le serveur !");
            main.getDatabase().createAccount(player);
        } else {
            event.setJoinMessage(ChatUtility.format("[&a+&r] ") + player.getName());
        }

        main.getPlayerManager().addPlayerData(player);
        main.getPlayerManager().sendPlayerToHub(player);
        player.setPlayerListHeader(ChatUtility.format("Bienvenue sur &9GDLCA Minigames&f !"));
        player.setPlayerListFooter(ChatUtility.format("Discord : &fdiscord.gg/vFjPYC4Mj8", ChatUtility.AQUA));
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        event.setQuitMessage(ChatUtility.format("[&c-&r] ") + player.getName());
        main.getPlayerManager().removePlayerInHub(player);
        main.getPlayerManager().removePlayerData(player);
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(cmd -> blockedCommands.contains(cmd.toLowerCase()));
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().toLowerCase().split(" ");

        if (blockedCommands2.contains(args[0]))
            event.setCancelled(true);
    }
}