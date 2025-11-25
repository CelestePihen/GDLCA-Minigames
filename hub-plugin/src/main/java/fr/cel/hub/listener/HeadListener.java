package fr.cel.hub.listener;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.event.WinterPlayerData;
import fr.cel.hub.Hub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class HeadListener extends HListener {

    public HeadListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull final PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && block.getType() == Material.PLAYER_HEAD) {

            Player player = event.getPlayer();
            WinterPlayerData playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player).getWinterPlayerData();

            int headId = main.getHeadManager().getHeadLocations().get(block.getLocation());

            if (playerData.getHeads().contains(headId)) {
                sendMessageWithPrefix(player, Component.text("Vous avez déjà récupéré cette tête !", NamedTextColor.RED));
            } else {
                playerData.addHeadFound(headId);
                playerData.addWinterPoints(5);
                sendMessageWithPrefix(player, Component.text("Vous avez récupéré une tête (id: " + headId + ") !", NamedTextColor.GREEN));
            }
        }
    }

}