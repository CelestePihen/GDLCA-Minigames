package fr.cel.gameapi.utils;

import org.bukkit.ChatColor;

public class ChatUtility {

    public final static String BLACK = "&0";
    public final static String DARK_BLUE = "&1";
    public final static String DARK_GREEN = "&2";
    public final static String DARK_AQUA = "&3";
    public final static String DARK_RED = "&4";
    public final static String DARK_PURPLE = "&5";
    public final static String GOLD = "&6";
    public final static String GRAY = "&7";
    public final static String DARK_GRAY = "&8";
    public final static String BLUE = "&9";
    public final static String GREEN = "&a";
    public final static String AQUA = "&b";
    public final static String RED = "&c";
    public final static String PINK = "&d";
    public final static String YELLOW = "&e";
    public final static String WHITE = "&f";
    public final static String RANDOM = "&k";
    public final static String BOLD = "&l";
    public final static String STRIKETHROUGH = "&m";
    public final static String UNDERLINE = "&n";
    public final static String ITALIC = "&o";
    public final static String RESET = "&r";

    /**
    * Mettre de la couleur pour un texte en mettant le symbole & suivi du caractère associé
    * @param str Le texte que vous voulez mettre en couleur
    * @return Renvoie le texte avec la couleur
    */
    public static String format(String str) {
        return ChatColor.translateAlternateColorCodes('&', ChatColor.RESET + str);
    }

    /**
     * Ne sert qu'à mettre une seule couleur pour un texte (mais le symbole & est quand même utilisable)
     * @param str Le texte que vous voulez mettre en couleur
     * @param color La couleur
     * @return Renvoie le texte avec la couleur
     */
    public static String format(String str, String color) {
        return format(color + str);
    }

    /**
    * Enlève le formattage des couleurs sur un texte
    * @param str Le texte que vous voulez sans couleur
    * @return Renvoie le texte sélectionné sans le formattage des couleurs
    */
    public static String stripColor(String str) {
        return ChatColor.stripColor(str);
    }

}