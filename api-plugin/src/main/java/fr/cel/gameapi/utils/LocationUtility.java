package fr.cel.gameapi.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

@UtilityClass
public class LocationUtility {

    /**
     * Converts a string into a Location instance.
     *
     * @param string The string containing the coordinates, formatted as "x,y,z".
     * @return A Location instance corresponding to the specified coordinates.
     *
     * @throws NumberFormatException If any part of the string cannot be converted to a double.
     * @throws ArrayIndexOutOfBoundsException If the string is incorrectly formatted (less than three parts).
     */
    public Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");

        double x = Double.parseDouble(parsedLoc[0]);
        double y = Double.parseDouble(parsedLoc[1]);
        double z = Double.parseDouble(parsedLoc[2]);

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

    /**
     * Converts a configuration string into a Location instance.
     *
     * @param location The string representing the configuration key for the coordinates.
     * @return A Location instance corresponding to the specified coordinates.
     *
     * @throws NumberFormatException If any part of the string cannot be converted to a double.
     */
    public Location parseConfigToLoc(YamlConfiguration config, String location) {
        double x = config.getDouble(location + ".x");
        double y = config.getDouble(location + ".y");
        double z = config.getDouble(location + ".z");

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

    /**
     * Converts a Location into a string.
     *
     * @param loc The Location containing the coordinates.
     * @return A string representing the specified coordinates.
     */
    public static String parseLocToString(Location loc) {
        return loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    /**
     * Stores a Location as a string in a configuration.
     *
     * @param config The configuration where the Location should be stored.
     * @param path The path in the configuration where the Location should be stored.
     * @param loc The Location containing the coordinates.
     */
    public static void parseLocToConfig(YamlConfiguration config, String path, Location loc) {
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
    }

}