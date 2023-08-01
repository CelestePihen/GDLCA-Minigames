package fr.cel.eldenrpg.utils;

import fr.cel.eldenrpg.EldenRPG;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public class Replacement {

    /**
     * Permet d'envoyer un message (avec l'API Adventure)
     * @param player Le joueur
     * @param message Le message
     */
    protected void sendMessage(Player player, String message) {
        player.sendMessage(Component.text(message));
    }

    /**
     * Permet d'envoyer un message avec le prefix (avec l'API Adventure)
     * @param player Le joueur
     * @param message Le message
     */
    protected void sendMessageWithPrefix(Player player, String message) {
        player.sendMessage(Component.text(EldenRPG.getEldenRPG().getPrefix() + message));
    }

    /**
     * Permet d'envoyer un titre avec des String
     * @param player Le joueur
     * @param title Le titre
     * @param subtitle Le sous-titre
     * @param fadeIn Le nombre de secondes qu'a le titre pour apparaître
     * @param stayOn Le nombre de secondes que le titre reste
     * @param fadeOut Le nombre de secondes qu'a le titre pour disparaître
     */
    protected void sendTitle(Player player, String title, String subtitle, int fadeIn, int stayOn, int fadeOut) {
        final Title.Times times = Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stayOn), Duration.ofSeconds(fadeOut));
        final Title t = Title.title(Component.text(title), Component.text(subtitle), times);
        player.showTitle(t);
    }

    /**
     * Permet d'envoyer un titre avec des Component
     * @param player Le joueur
     * @param title Le titre
     * @param subtitle Le sous-titre
     * @param fadeIn Le nombre de secondes qu'a le titre pour apparaître
     * @param stayOn Le nombre de secondes que le titre reste
     * @param fadeOut Le nombre de secondes qu'a le titre pour disparaître
     */
    protected void sendTitle(Player player, Component title, Component subtitle, int fadeIn, int stayOn, int fadeOut) {
        final Title.Times times = Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stayOn), Duration.ofSeconds(fadeOut));
        final Title t = Title.title(title, subtitle, times);
        player.showTitle(t);
    }

}
