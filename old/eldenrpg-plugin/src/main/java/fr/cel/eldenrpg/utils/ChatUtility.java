package fr.cel.eldenrpg.utils;

import org.bukkit.ChatColor;

public class ChatUtility {

    /**
    * Mettre de la couleur en mettant &
    * @param str Le texte que vous voulez mettre en couleur
    */
    public static String format(String str) {
        return ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
    * Mettre d'enlever la couleur
    * @param str Le texte où vous voulez enlever la couleur
    */
    public static String stripColor(String str) {
        return ChatColor.stripColor(str);
    }

}