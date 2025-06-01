package fr.cel.gameapi.listeners;

import org.bukkit.ServerLinks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLinksSendEvent;

import java.net.URI;
import java.net.URISyntaxException;

public final class ServerListeners implements Listener {

    @EventHandler
    public void onServerLinksSend(PlayerLinksSendEvent event) {
        try {
            event.getLinks().addLink(ServerLinks.Type.COMMUNITY, new URI("https://discord.gg/vFjPYC4Mj8"));
        } catch (URISyntaxException ignored) {}
    }

}