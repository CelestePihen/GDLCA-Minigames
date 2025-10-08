package fr.cel.gameapi.utils.zone;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class ZoneUtils {

    /**
     * Checks if a location is inside a cuboid defined by two corners.
     * @param loc The location to check
     * @param corner1 One corner of the cuboid
     * @param corner2 The opposite corner of the cuboid
     * @return true if inside the cuboid
     */
    public static boolean isInZone(Location loc, Location corner1, Location corner2) {
        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

}
