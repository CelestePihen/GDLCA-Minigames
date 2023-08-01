package fr.cel.hub.listener;

import fr.cel.cachecache.manager.CCArena;
import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.hub.Hub;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.pvp.manager.PVPGameManager;
import fr.cel.pvp.manager.arena.PVPArena;
import fr.cel.valocraft.manager.ValoGameManager;
import fr.cel.valocraft.manager.arena.ValoArena;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MiniGameListener extends HListener {

    public MiniGameListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void interactSign(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        if (block != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockState blockState = block.getState();

            if (blockState instanceof Sign sign) {
                switch (ChatUtility.stripColor(sign.getSide(Side.FRONT).getLine(1))) {
                    // on regarde si le texte de la 2ème ligne est égal à "Cache-Cache"
                    case "Cache-Cache" -> {
                        // on regarde si la 3ème ligne a un nom d'arène
                        CCArena arena = CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName(ChatUtility.stripColor(sign.getSide(Side.FRONT).getLine(2)));
                        // si l'arène n'est pas null alors on peut ajouter le joueur dans l'arène associé
                        if (arena != null) arena.addPlayer(player);
                        // sinon on dit au joueur que l'arène n'existe pas et d'avertir les admins s'il y a un bug
                        else player.sendMessage(CCGameManager.getPrefix() + "Cette carte Cache-Cache n'existe pas. Merci de contacter un admin si vous pensez que cela est un bug.");
                    }

                    // pareil qu'au dessus mais là on regarde pour "Valocraft"
                    case "Valocraft" -> {
                        ValoArena arena = ValoGameManager.getGameManager().getArenaManager().getArenaByDisplayName(ChatUtility.stripColor(sign.getSide(Side.FRONT).getLine(2)));
                        if (arena != null) arena.addPlayer(player);
                        else player.sendMessage(ValoGameManager.getGameManager().getPrefix() + "Cette carte Valocraft n'existe pas. Merci de contacter un admin si vous pensez que cela est un bug.");
                    }

                    // pareil mais là on regarde pour "PVP"
                    case "PVP" -> {
                        PVPArena arena = PVPGameManager.getGameManager().getArenaManager().getArenaByDisplayName(ChatUtility.stripColor(sign.getSide(Side.FRONT).getLine(2)));
                        if (arena != null) arena.addPlayer(player);
                        else player.sendMessage(PVPGameManager.getGameManager().getPrefix() + "Cette arène PVP n'existe pas. Merci de contacter un admin si vous pensez que cela est un bug.");
                    }

                }
            }
        }
    }

}