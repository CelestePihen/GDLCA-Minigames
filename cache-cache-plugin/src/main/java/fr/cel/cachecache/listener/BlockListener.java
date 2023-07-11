package fr.cel.cachecache.listener;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.cel.cachecache.manager.Arena;
import fr.cel.cachecache.manager.GameManager;
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

        // si le bloc n'est pas de l'air et si l'action est un clic droit sur un bloc
        if (block != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockState blockState = block.getState();

            // si le blockstate est un Sign
            if (blockState instanceof Sign) {
                Sign sign = (Sign) blockState;
                // on regarde le texte de la 2ème ligne s'il est égal à "Cache-Cache"
                if (ChatUtility.stripColor(sign.getSide(Side.FRONT).getLine(1)).equalsIgnoreCase("Cache-Cache")) {
                    // on regarde si la 3ème ligne a un nom d'arène
                    Arena arena = gameManager.getArenaManager().getArenaByDisplayName(ChatUtility.stripColor(sign.getSide(Side.FRONT).getLine(2)));
                    // si l'arène est bien un arène alors on peut ajouter le joueur dans l'arène associé
                    if (arena instanceof Arena) arena.addPlayer(player);
                    // sinon on lui dit que cette carte Cache-Cache n'existe pas
                    else player.sendMessage(GameManager.getPrefix() + "Cette carte Cache-Cache n'existe pas. Merci de contacter un admin si vous pensez que cela est un bug.");
                }
            }
        }
    }

}