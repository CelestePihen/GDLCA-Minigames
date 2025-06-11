package fr.cel.decorationsplugin.listener;

import fr.cel.decorationsplugin.DecorationsPlugin;
import fr.cel.decorationsplugin.manager.Decoration;
import fr.cel.decorationsplugin.manager.DecorationsManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class DecorationsListener implements Listener {

    private final DecorationsManager manager;

    public DecorationsListener(DecorationsManager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!player.isOp()) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        Decoration decoration = manager.getFromItem(itemStack);
        if (decoration == null) return;

        event.setCancelled(true);

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.sendMessage(DecorationsPlugin.getPrefix() + "§cVous devez être en mode créatif pour utiliser les décorations.");
            return;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.getType() == Material.BARRIER) return;
        Location base = targetBlock.getLocation().add(0, 1, 0);

        // ⚠️ Forcer l’orientation VERS LE HAUT
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
    }

    private Rotation getRotationFromYaw(float yaw) {
        // Normalise l'angle pour être entre 0 et 360
        yaw = (yaw % 360 + 360) % 360;

        // Décalage de 180° pour compenser l'orientation inversée du ItemFrame au sol
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
