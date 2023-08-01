package fr.cel.hub.listener;

import fr.cel.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener extends HListener {

    public ChatListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void chat(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        if (message.equalsIgnoreCase("/tp Cel___") || message.equalsIgnoreCase("/minecraft:tp Cel___") || message.equalsIgnoreCase("/tp " + player.getName() + "Cel___") || message.equalsIgnoreCase("/minecraft:tp " + player.getName() + "Cel___")) {
            Player cel = Bukkit.getPlayer("Cel___");
            if (cel == null) return;

            if (main.getPlayerManager().containsPlayerInInstitution(cel)) {
                sendMessageWithPrefix(player, "Vous ne pouvez pas vous téléporter à Cel___ pour le moment 😏.");
                event.setCancelled(true);
            }

        }

    }
    
}