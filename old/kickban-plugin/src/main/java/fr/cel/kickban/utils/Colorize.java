package fr.cel.kickban.utils;

import org.bukkit.ChatColor;

public class Colorize {

    /**
    * Mettre de la couleur en mettant &
    * @param str Le texte que vous voulez mettre en couleur
    */
    public static String format(String str) {
        return ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * Enlever la couleur du texte
     * @param str Le texte où vous voulez enlever la couleur
     */
    public static String stripColor(String str) {
        return ChatColor.stripColor(str);
    }

}