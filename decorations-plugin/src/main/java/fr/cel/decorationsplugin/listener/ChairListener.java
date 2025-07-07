package fr.cel.decorationsplugin.listener;

import fr.cel.decorationsplugin.manager.Chair;
import fr.cel.decorationsplugin.manager.ChairManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

public class ChairListener implements Listener {

    private final ChairManager chairManager;

    public ChairListener(ChairManager chairManager) {
        this.chairManager = chairManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Location blockLocation = clickedBlock.getLocation();
        if (!chairManager.isChair(blockLocation)) return;

        Player player = event.getPlayer();

        if (chairManager.isPlayerSeated(player)) return;

        Chair chair = chairManager.getChairAt(blockLocation);
        if (chair != null && chair.isOccupied()) {
            player.sendMessage("§cCette chaise est déjà occupée !");
        } else {
            chairManager.sitPlayer(player, blockLocation);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (chairManager.isPlayerSeated(player)) chairManager.standUpPlayer(player);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (chairManager.isPlayerSeated(player)) chairManager.standUpPlayer(player);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (chairManager.isPlayerSeated(player)) chairManager.standUpPlayer(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (chairManager.isPlayerSeated(player)) chairManager.standUpPlayer(player);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        if (chairManager.isPlayerSeated(player)) {
            Chair chair = chairManager.getChairAt(player);
            chairManager.standUpPlayer(player);

            if (chair != null) teleportPlayerToExitLocation(player, chair);
        }
    }

    /**
     * Téléporte le joueur qui se situe dans une chaise à l'emplacement voulu
     * @param player Le joueur assis sur la chaise
     * @param chair L'instance de la chaise sur laquelle est le joueur
     */
    private void teleportPlayerToExitLocation(Player player, Chair chair) {
        Location chairLocation = chair.getLocation();
        Block block = chairLocation.getBlock();

        Location exitLocation = null;

        if (block.getBlockData() instanceof Directional directionalBlock) {
            BlockFace facing = directionalBlock.getFacing();
            exitLocation = getExitLocationFromDirection(chairLocation, facing);
        }

        else if (block.getType() == Material.BARRIER) {
            Location searchCenter = chairLocation.clone().add(0.5, 0.5, 0.5);

            for (Entity entity : chairLocation.getWorld().getNearbyEntities(searchCenter, 2.0, 2.0, 2.0)) {

                if (entity instanceof ItemFrame itemFrame) {
                    if (itemFrame.getFacing() == BlockFace.UP) {
                        Rotation rotation = itemFrame.getRotation();
                        exitLocation = getExitLocationFromRotation(chairLocation, rotation);
                        break;
                    }
                }
            }
        }

        exitLocation = findSafeLocation(exitLocation);

        if (exitLocation != null) {
            player.teleport(exitLocation);
        }
    }

    /**
     * Permet d'obtenir l'emplacement selon la direction de l'escalier
     * @param chairLocation L'emplacement de la chaise
     * @param direction La direction de la chaise
     * @return Retourne l'emplacement où le joueur devra être téléporté
     */
    private Location getExitLocationFromDirection(Location chairLocation, BlockFace direction) {
        double offsetX = -direction.getModX();
        double offsetZ = -direction.getModZ();

        // Pour les diagonales, normalise la distance pour éviter d'aller trop loin
        if (offsetX != 0 && offsetZ != 0) {
            offsetX *= 0.8;
            offsetZ *= 0.8;
        }

        Location exitLoc = chairLocation.clone().add(offsetX + 0.5, 0, offsetZ + 0.5);
        exitLoc.setY(chairLocation.getY());
        return exitLoc;
    }

    /**
     * Permet d'obtenir l'emplacement selon la rotation de l'item frame
     * @param chairLocation L'emplacement de la chaise
     * @param itemFrameRotation La rotation de l'item frame de la chaise
     * @return Retourne l'emplacement où le joueur devra être téléporté
     */
    private Location getExitLocationFromRotation(Location chairLocation, Rotation itemFrameRotation) {
        BlockFace exitDirection = switch (itemFrameRotation) {
            case NONE -> BlockFace.NORTH;
            case CLOCKWISE_45 -> BlockFace.NORTH_EAST;
            case CLOCKWISE -> BlockFace.EAST;
            case CLOCKWISE_135 -> BlockFace.SOUTH_EAST;
            case FLIPPED -> BlockFace.SOUTH;
            case FLIPPED_45 -> BlockFace.SOUTH_WEST;
            case COUNTER_CLOCKWISE -> BlockFace.WEST;
            case COUNTER_CLOCKWISE_45 -> BlockFace.NORTH_WEST;
        };

        return getExitLocationFromDirection(chairLocation, exitDirection);
    }

    /**
     * Vérifie si le joueur ne sera pas téléporté à un endroit indésirable
     * @param location L'emplacement où le joueur devait, peut-être, être téléporté
     * @return Retourne l'emplacement où le joueur sera téléporté
     */
    private Location findSafeLocation(Location location) {
        if (location == null) return null;

        // vérifier si la location est sûre (pas de blocs solides)
        Block blockAt = location.getBlock();
        Block blockAbove = location.clone().add(0, 1, 0).getBlock();
        Block blockBelow = location.clone().add(0, -1, 0).getBlock();

        // si les blocs au niveau des pieds et de la tête sont libres et qu'il y a un sol
        if ((!blockAt.getType().isSolid() || blockAt.getType() == Material.BARRIER) &&
                (!blockAbove.getType().isSolid() || blockAbove.getType() == Material.BARRIER) &&
                blockBelow.getType().isSolid() && blockBelow.getType() != Material.BARRIER) {
            return location;
        }

        // essayer de trouver un endroit sûr près de la location
        for (int y = -2; y <= 2; y++) {
            Location testLoc = location.clone().add(0, y, 0);
            Block testBlock = testLoc.getBlock();
            Block testAbove = testLoc.clone().add(0, 1, 0).getBlock();
            Block testBelow = testLoc.clone().add(0, -1, 0).getBlock();

            if ((!testBlock.getType().isSolid() || testBlock.getType() == Material.BARRIER) &&
                    (!testAbove.getType().isSolid() || testAbove.getType() == Material.BARRIER) &&
                    testBelow.getType().isSolid() && testBelow.getType() != Material.BARRIER) {
                return testLoc;
            }
        }

        return location;
    }

}