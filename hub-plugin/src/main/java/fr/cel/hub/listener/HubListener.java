package fr.cel.hub.listener;

import fr.cel.hub.Hub;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class HubListener implements Listener {

    protected final Hub main;

    public HubListener(Hub main) {
        this.main = main;
    }

    protected void sendMessage(Player player, String message) {
        player.sendMessage(Component.text(message));
    }

    protected void sendMessageWithPrefix(Player player, String message) {
        player.sendMessage(Component.text(main.getPrefix() + message));
    }

}
