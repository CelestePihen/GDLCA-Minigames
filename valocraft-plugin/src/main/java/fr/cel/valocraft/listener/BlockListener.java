package fr.cel.valocraft.listener;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.cel.valocraft.manager.GameManager;
import fr.cel.valocraft.manager.arena.Arena;
import fr.cel.hub.utils.ChatUtility;

public class BlockListener implements Listener {

    private GameManager gameManager;

    public BlockListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void interactSign(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        if (block != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockState blockState = block.getState();

            if (blockState instanceof Sign) {
                Sign sign = (Sign) blockState;
                if (ChatUtility.stripColor(sign.getSide(Side.FRONT).getLine(1)).equalsIgnoreCase("ValoCraft")) {
                    Arena arena = gameManager.getArenaManager().getArenaByDisplayName(ChatUtility.stripColor(sign.getSide(Side.FRONT).getLine(2)));
                    if (arena instanceof Arena) arena.addPlayer(player);
                    else player.sendMessage(gameManager.getPrefix() + "Cette arène ValoCraft n'existe pas. Merci de contacter un admin pour corriger cela.");
                }
            }
        }
    }

}