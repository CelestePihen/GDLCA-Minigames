package fr.cel.gameapi.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
@Deprecated(since = "1.3")
public final class ChatUtility {

    public final String BLACK = "&0";
    public final String DARK_BLUE = "&1";
    public final String DARK_GREEN = "&2";
    public final String DARK_AQUA = "&3";
    public final String DARK_RED = "&4";
    public final String DARK_PURPLE = "&5";
    public final String GOLD = "&6";
    public final String GRAY = "&7";
    public final String DARK_GRAY = "&8";
    public final String BLUE = "&9";
    public final String GREEN = "&a";
    public final String AQUA = "&b";
    public final String RED = "&c";
    public final String PINK = "&d";
    public final String YELLOW = "&e";
    public final String WHITE = "&f";
    public final String RANDOM = "&k";
    public final String BOLD = "&l";
    public final String STRIKETHROUGH = "&m";
    public final String UNDERLINE = "&n";
    public final String ITALIC = "&o";
    public final String RESET = "&r";

    /**
    * Mettre de la couleur pour un texte en mettant le symbole Esperluette suivi du caractère associé
    * @param str Le texte que vous voulez mettre en couleur
    * @return Renvoie le texte avec la couleur
    */
    public String format(String str) {
        return ChatColor.translateAlternateColorCodes('&', ChatColor.RESET + str);
    }

    /**
     * Ne sert qu'à mettre une seule couleur pour un texte (mais le symbole Esperluette est quand même utilisable)
     * @param str Le texte que vous voulez mettre en couleur
     * @param color La couleur
     * @return Renvoie le texte avec la couleur
     */
    public String format(String str, String color) {
        return format(color + str);
    }

    /**
    * Enlève le formattage des couleurs sur un texte
    * @param str Le texte que vous voulez sans couleur
    * @return Renvoie le texte sélectionné sans le formattage des couleurs
    */
    public String stripColor(String str) {
        return ChatColor.stripColor(str);
    }

}