package fr.cel.gameapi.utils;

import lombok.Getter;
import org.bukkit.ChatColor;

public class ChatUtility {

    /**
    * Mettre de la couleur pour un texte en mettant le symbole & suivi du caractère associé
    * @param str Le texte que vous voulez mettre en couleur
    * @return Renvoie le texte avec la couleur
    */
    public static String format(String str) {
        return ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * Ne sert qu'à mettre une seule couleur pour un texte (mais le symbole & est quand même utilisable)
     * @param str Le texte que vous voulez mettre en couleur
     * @param color La couleur
     * @return Renvoie le texte avec la couleur
     */
    public static String format(String str, ChatUtility.UtilityColor color) {
        return format(color.character + str);
    }

    /**
    * Enlève le formattage des couleurs sur un texte
    * @param str Le texte que vous voulez sans couleur
    * @return Renvoie le texte sélectionné sans le formattage des couleurs
    */
    public static String stripColor(String str) {
        return ChatColor.stripColor(str);
    }

    public enum UtilityColor {
        BLACK("&0"),
        DARK_BLUE("&1"),
        DARK_GREEN("&2"),
        DARK_AQUA("&3"),
        DARK_RED("&4"),
        DARK_PURPLE("&5"),
        GOLD("&6"),
        GRAY("&7"),
        DARK_GRAY("&8"),
        BLUE("&9"),
        GREEN("&a"),
        AQUA("&b"),
        RED("&c"),
        PINK("&d"),
        YELLOW("&e"),
        WHITE("&f"),
        RANDOM("&k"),
        BOLD("&l"),
        STRIKETHROUGH("&m"),
        UNDERLINE("&n"),
        ITALIC("&o"),
        RESET("&r")
        ;

        @Getter private final String character;

        UtilityColor(String character) {
            this.character = character;
        }

    }

}