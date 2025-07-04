package fr.cel.gameapi.utils;

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

    /**
     * Convertit une Location en chaîne de caractères.
     *
     * @param loc La Location contenant les coordonnées
     * @return Une chaîne de caractères correspondant aux coordonnées spécifiées.
     */
    public static String parseLocToString(Location loc) {
        return loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    /**
     * Met une Location en chaîne de caractères dans une configuration.
     *
     * @param config La configuration où vous voulez mettre la Location
     * @param path Le chemin où vous voulez mettre la Location
     * @param loc La Location contenant les coordonnées
     */
    public static void parseLocToConfig(YamlConfiguration config, String path, Location loc) {
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
    }

}
