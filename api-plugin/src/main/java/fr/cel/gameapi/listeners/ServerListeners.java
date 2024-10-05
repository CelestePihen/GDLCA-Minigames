package fr.cel.gameapi.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLinksSendEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.net.URI;
import java.net.URISyntaxException;

public class ServerListeners implements Listener {

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.setMaxPlayers(Bukkit.getMaxPlayers() + 1);
    }

    @EventHandler
    public void onServerLinksSend(PlayerLinksSendEvent event) {
        try {
            event.getLinks().addLink("Discord", new URI("https://discord.gg/vFjPYC4Mj8"));
        } catch (URISyntaxException ignored) {}
    }

}