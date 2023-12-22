package fr.cel.hub.utils;

import lombok.Getter;
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
        STRIKRTHROUGH("&m"),
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