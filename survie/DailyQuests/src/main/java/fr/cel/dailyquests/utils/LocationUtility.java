package fr.cel.dailyquests.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

@UtilityClass
public class LocationUtility {

    /**
     * Convertit une chaîne de caractères en une instance de Location.
     *
     * @param string La chaîne de caractères contenant les coordonnées, formatée sous la forme "x,y,z".
     * @return Une instance de Location correspondant aux coordonnées spécifiées.
     *
     * @throws NumberFormatException Si l'une des parties de la chaîne ne peut pas être convertie en double.
     * @throws ArrayIndexOutOfBoundsException Si la chaîne n'a pas le bon format (moins de trois parties).
     */
    public Location parseStringToLoc(YamlConfiguration config, String string) {
        String[] parsedLoc = string.split(",");

        double x = Double.parseDouble(parsedLoc[0]);
        double y = Double.parseDouble(parsedLoc[1]);
        double z = Double.parseDouble(parsedLoc[2]);

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

    /**
     * Convertit une chaîne de caractères de configuration en une instance de Location.
     *
     * @param location La chaîne de caractères représentant la clé de configuration des coordonnées.
     * @return Une instance de Location correspondant aux coordonnées spécifiées.
     *
     * @throws NumberFormatException Si l'une des parties de la chaîne ne peut pas être convertie en double.
     */
    public Location parseConfigToLoc(YamlConfiguration config, String location) {
        double x = config.getDouble(location + ".x");
        double y = config.getDouble(location + ".y");
        double z = config.getDouble(location + ".z");

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

}
