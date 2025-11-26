package fr.cel.hub.manager.event.winter2025;

import com.destroystokyo.paper.profile.ProfileProperty;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.event.WinterPlayerData;
import fr.cel.hub.Hub;
import fr.cel.hub.listener.HListener;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class HeadListener extends HListener {

    public HeadListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull final PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && event.getHand() == EquipmentSlot.HAND
                && event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.PLAYER_HEAD) {

            Skull skull = (Skull) block.getState();
            ResolvableProfile profile = skull.getProfile();
            if (profile == null) return;

            boolean isGiftHead = false;
            for (ProfileProperty property : profile.properties()) {
                if (property.getValue().equalsIgnoreCase(HeadManager.VALUE_GIFT_TEXTURE)) {
                    isGiftHead = true;
                    break;
                }
            }

            if (!isGiftHead) return;

            Player player = event.getPlayer();
            WinterPlayerData playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player).getWinterPlayerData();

            int headId = main.getHeadManager().getHeadLocations().get(block.getLocation());

            if (playerData.getHeads().contains(headId)) {
                sendMessageWithPrefix(player, Component.text("Vous avez déjà récupéré cette tête !", NamedTextColor.RED));
            } else {
                playerData.addHeadFound(headId);

                int currentHeads = playerData.getHeads().size();
                if (currentHeads <= 10) {
                    playerData.addWinterPoints(10);
                } else if (currentHeads <= 20) {
                    playerData.addWinterPoints(15);
                } else {
                    playerData.addWinterPoints(20);
                }

                playerData.addWinterPoints(5);
                sendMessageWithPrefix(player, Component.text("Vous avez récupéré une tête (id: " + headId + ") !", NamedTextColor.GREEN));
            }
        }
    }

}