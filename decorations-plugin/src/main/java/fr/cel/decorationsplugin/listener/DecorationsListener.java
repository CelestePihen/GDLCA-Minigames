package fr.cel.decorationsplugin.listener;

import fr.cel.decorationsplugin.DecorationsPlugin;
import fr.cel.decorationsplugin.manager.Decoration;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DecorationsListener implements Listener {

    private final DecorationsPlugin main;

    public DecorationsListener(DecorationsPlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) return;

        Block block = event.getBlock();

        Material material = block.getType();
        if (!material.name().contains("STAIRS") && material != Material.BARRIER) return;

        Location blockLocation = block.getLocation();

        for (Entity entity : blockLocation.getWorld().getNearbyEntities(blockLocation, 1, 1, 1)) {
            if (entity instanceof ItemFrame itemFrame) {
                ItemMeta itemMeta = itemFrame.getItem().getItemMeta();
                if (itemMeta != null && itemMeta.getItemModel() != null) {
                    Decoration decoration = main.getDecorationsManager().getFromItem(itemFrame.getItem());

                    for (double x = blockLocation.getBlockX() - decoration.sizeX(); x < blockLocation.getBlockX() + decoration.sizeX(); x++) {
                        for (double y = blockLocation.getBlockY() - decoration.sizeY(); y < blockLocation.getBlockY() + decoration.sizeY(); y++) {
                            for (double z = blockLocation.getBlockZ() - decoration.sizeZ(); z < blockLocation.getBlockZ() + decoration.sizeZ(); z++) {
                                Block block2 = blockLocation.getWorld().getBlockAt(new Location(blockLocation.getWorld(), x, y ,z));
                                if (block2.getType() == Material.BARRIER) {
                                    block2.setType(Material.AIR);
                                }
                            }
                        }
                    }

                    entity.remove();
                }
            }

            if (entity instanceof ArmorStand && main.getChairManager().isChair(blockLocation)) entity.remove();
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!player.isOp()) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        Decoration decoration = main.getDecorationsManager().getFromItem(itemStack);
        if (decoration == null) return;

        event.setCancelled(true);

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.sendMessage(fr.cel.decorationsplugin.DecorationsPlugin.getPrefix() + "§cVous devez être en mode créatif pour utiliser les décorations.");
            return;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.getType() == Material.BARRIER) return;
        Location base = targetBlock.getLocation().add(0, 1, 0);

        Location frameLoc = base.clone().add(0.5 * decoration.sizeX(), 0.5, 0.5 * decoration.sizeZ());
        ItemFrame frame = (ItemFrame) player.getWorld().spawnEntity(frameLoc, EntityType.ITEM_FRAME);
        frame.setFacingDirection(BlockFace.UP, true); // Force la direction
        frame.setInvulnerable(true);
        frame.setFixed(true);
        frame.setVisible(false);
        frame.setCustomNameVisible(false);
        frame.setItem(decoration.getDisplayItem());

        Rotation rotation = getRotationFromYaw(player.getLocation().getYaw());
        frame.setRotation(rotation);

        for (int x = 0; x < decoration.sizeX(); x++) {
            for (int y = 0; y < decoration.sizeY(); y++) {
                for (int z = 0; z < decoration.sizeZ(); z++) {
                    Location loc = base.clone().add(x, y, z);
                    if (loc.getBlock().getType() == Material.AIR) {
                        loc.getBlock().setType(Material.BARRIER);
                    }
                }
            }
        }

        if (decoration.sittable()) main.getChairManager().addChair(base);
    }

    /**
     * Permet d'obtenir la rotation de l'item frame selon le regard du joueur
     * @param yaw Le regard du joueur
     * @return Retourne la rotation que que l'item frame aura
     */
    private Rotation getRotationFromYaw(float yaw) {
        // normalise l'angle pour être entre 0 et 360
        yaw = (yaw % 360 + 360) % 360;

        // décalage de 180° pour compenser l'orientation inversée du ItemFrame au sol
        yaw = (yaw + 180) % 360;

        if (yaw >= 337.5 || yaw < 22.5) return Rotation.NONE;
        else if (yaw < 67.5) return Rotation.CLOCKWISE_45;
        else if (yaw < 112.5) return Rotation.CLOCKWISE;
        else if (yaw < 157.5) return Rotation.CLOCKWISE_135;
        else if (yaw < 202.5) return Rotation.FLIPPED;
        else if (yaw < 247.5) return Rotation.FLIPPED_45;
        else if (yaw < 292.5) return Rotation.COUNTER_CLOCKWISE;
        else return Rotation.COUNTER_CLOCKWISE_45;
    }

}