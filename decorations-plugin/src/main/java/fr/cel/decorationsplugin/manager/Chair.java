package fr.cel.decorationsplugin.manager;

import fr.cel.decorationsplugin.DecorationsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public final class Chair {

    @Getter private final UUID chairId;
    private final Location location;

    @Getter @Setter private ArmorStand armorStand;
    @Getter private UUID seatedPlayer;

    public Chair(Location location) {
        this.location = location.clone();
        this.chairId = UUID.randomUUID();
        this.seatedPlayer = null;
    }

    public Chair(Location location, UUID chairId) {
        this.location = location.clone();
        this.chairId = chairId;
        this.seatedPlayer = null;
    }

    /**
     * Créé un Armor Stand permettant à un joueur de pouvoir s'asseoir
     */
    public void createArmorStand() {
        if (armorStand != null && armorStand.isValid()) return;

        double offsetX = 0.5;
        double offsetZ = 0.5;

        Block block = location.getBlock();
        if (block.getBlockData() instanceof Stairs stairData) {
            BlockFace facing = stairData.getFacing();
            switch (facing) {
                case NORTH -> offsetZ = 0.7;
                case SOUTH -> offsetZ = 0.3;
                case EAST -> offsetX = 0.3;
                case WEST -> offsetX = 0.7;
                default -> {
                    return;
                }
            }
        }

        Location sitLocation = location.clone().add(offsetX, -1.5, offsetZ);
        armorStand = location.getWorld().spawn(sitLocation, ArmorStand.class);

        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setInvulnerable(true);
        armorStand.setCustomName("Chair_" + chairId.toString());
        armorStand.setCustomNameVisible(false);
        armorStand.setPersistent(true);

        // Metadata pour l'identifier
        armorStand.setMetadata("stairchair", new FixedMetadataValue(DecorationsPlugin.getInstance(), chairId.toString()));
    }

    /**
     * Supprime l'ArmorStand
     */
    public void removeArmorStand() {
        if (armorStand != null && armorStand.isValid()) {
            if (seatedPlayer != null) standUp();
            armorStand.remove();
            armorStand = null;
        }
    }

    /**
     * Fait asseoir un joueur
     * @param player Le joueur à faire asseoir
     * @return Retourne vrai si le joueur a réussi à s'asseoir, faux alors un autre joueur ou lui-même est déjà assis
     */
    public boolean sitDown(Player player) {
        if (isOccupied()) return false;

        // on vérifie s'il y a bien un Armor Stand
        if (armorStand == null || !armorStand.isValid()) createArmorStand();

        armorStand.addPassenger(player);
        seatedPlayer = player.getUniqueId();
        return true;
    }

    /**
     * Fait lever le joueur
     */
    public void standUp() {
        if (seatedPlayer != null) {
            Player player = Bukkit.getPlayer(seatedPlayer);
            if (armorStand != null && armorStand.isValid() && player != null) armorStand.removePassenger(player);

            seatedPlayer = null;
        }
    }

    /**
     * Permet de vérifier si un joueur est assis sur la chaise
     * @return Retourne vrai s'il y a un joueur assit, faux s'il n'y en a pas
     */
    public boolean isOccupied() {
        return seatedPlayer != null && Bukkit.getPlayer(seatedPlayer) != null;
    }

    /**
     * Donne l'emplacement de la chaise
     * @return Retourne une instance de l'emplacement de la chaise
     * @see org.bukkit.Location
     */
    public Location getLocation() {
        return location.clone();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Chair chair = (Chair) obj;
        return location.equals(chair.location);
    }

}