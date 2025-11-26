package fr.cel.hub.listener;

import fr.cel.hub.Hub;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class HListener implements Listener {

    protected final Hub main;

    public HListener(Hub main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    /**
     * Permet d'envoyer un message avec le prefix
     * @param player Le joueur
     * @param message Le message
     */
    protected void sendMessageWithPrefix(Player player, Component message) {
        player.sendMessage(Hub.getPrefix().append(message));
    }

}