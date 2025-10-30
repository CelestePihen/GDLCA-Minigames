package fr.cel.gameapi.listeners;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.AdvancementsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            event.joinMessage(GameAPI.getPrefix().append(Component.text("Bienvenue à " + player.getName() + " sur le serveur !")));

            main.getPlayerManager().setNewPlayer(player.getUniqueId());
            main.getPlayerManager().getPlayersWhoWelcomed().clear();

            main.getAdvancementsManager().giveAdvancement(player, AdvancementsManager.Advancements.ROOT);
        } else {
            event.joinMessage(Component.text("[")
                    .append(Component.text("+", NamedTextColor.GREEN))
                    .append(Component.text("] ", NamedTextColor.WHITE))
                    .append(Component.text(player.getName()))
            );
        }

        main.getDatabase().createAccount(player);

        main.getPlayerManager().addPlayerData(player);
        main.getPlayerManager().sendPlayerToHub(player);

        player.sendPlayerListHeader(Component.text("Amusez-vous bien sur ", NamedTextColor.GREEN)
                .append(Component.text("GDLCA Minigames", NamedTextColor.AQUA))
                .append(Component.text(" !", NamedTextColor.WHITE)));

        player.sendPlayerListFooter(Component.text("Discord", NamedTextColor.DARK_AQUA).append(Component.text(": discord.gg/vFjPYC4Mj8", NamedTextColor.WHITE)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        event.quitMessage(Component.text("[")
                .append(Component.text("-", NamedTextColor.RED))
                .append(Component.text("] ", NamedTextColor.WHITE))
                .append(Component.text(player.getName()))
        );

        main.getPlayerManager().removePlayerInHub(player);
        main.getPlayerManager().removePlayerData(player);
    }

    @EventHandler
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().isOp()) return;

        for (String cmd : blockedCommands) {
            if (event.getMessage().toLowerCase().startsWith("/" + cmd)) event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerCommandSend(PlayerCommandSendEvent event) {
        if (!event.getPlayer().isOp())
            event.getCommands().removeAll(blockedCommands);
    }

}