package fr.cel.kickban.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import fr.cel.kickban.KickBan;

public class KickBanListener implements Listener {

    private KickBan main;

    public KickBanListener(KickBan main) {
        this.main = main;
    }

    @EventHandler
    public void interactSign(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equalsIgnoreCase("Compote_Fraise") || player.getName().equalsIgnoreCase("Agix0") || player.getName().equalsIgnoreCase("Samoonchild")) {
            if (event.getMessage().contains("/kick")) {
                player.sendMessage(main.getPrefix() + "Tu as essayé d'exclure quelqu'un là ?");
                event.setCancelled(true);
                return;
            }

            if (event.getMessage().contains("/ban") || event.getMessage().contains("/ban-ip")) {
                player.sendMessage(main.getPrefix() + "Tu as essayé de bannir quelqu'un là ?");
                event.setCancelled(true);
                return;
            }

            if (event.getMessage().contains("/deop")) {
                player.sendMessage(main.getPrefix() + "Tu as essayé de deop quelqu'un là ?");
                event.setCancelled(true);
                return;
            } 

        }

    }

}