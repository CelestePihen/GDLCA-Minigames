package fr.cel.hub.listener;

import fr.cel.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ChatListener extends HListener {

    private final ConcurrentHashMap<UUID, MessageData> lastMessages;
    private static final Pattern URL = Pattern.compile("\\b(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    public ChatListener(Hub main) {
        super(main);
        lastMessages = new ConcurrentHashMap<>();
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
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

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();

        if (message.startsWith("/")) return;

        Player player = event.getPlayer();
        long time = System.currentTimeMillis();

        MessageData last = lastMessages.get(player.getUniqueId());
        if (last != null) {
            if (last.isTooEarly(time)) {
                sendMessageWithPrefix(player, "Merci de ne pas envoyer de messages trop souvent.");
                event.setCancelled(true);
                return;
            } else if (last.isSame(message, time)) {
                sendMessageWithPrefix(player, "Merci de ne pas envoyer plusieurs fois le même message.");
                event.setCancelled(true);
                return;
            }
        }

        MessageData current = new MessageData();
        current.message = message;
        current.time = time;
        if (last != null) {
            lastMessages.replace(player.getUniqueId(), current);
        } else {
            lastMessages.put(player.getUniqueId(), current);
        }

        if (message.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)")) {
            sendMessageWithPrefix(player, "Merci de ne pas mettre d'adresse IP dans le chat !");
            event.setCancelled(true);
        }

        else if (URL.matcher(message).find()) {
            sendMessageWithPrefix(player, "Merci de ne pas mettre de lien dans le chat !");
            event.setCancelled(true);
        }

        else if (message.matches("[A-Z]{4,}")) {
            sendMessageWithPrefix(player, "Merci de ne pas mettre vos messages en majuscules !");
            event.setCancelled(true);
        }

    }


    private static class MessageData {
        public String message = "";
        public long time = 0;

        public boolean isSame(String message, long time) {
            boolean eq = this.message.equals(message);
            if (!eq) return false;
            return (this.time + 15000 > time); // 15 secondes entre chaque message identique
        }

        public boolean isTooEarly(long time) {
            return this.time + 1500 > time;
        }
    }

}