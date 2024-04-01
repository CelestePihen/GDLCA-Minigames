package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractCommand {

    private final String permission;
    private final boolean needPlayer;
    private final boolean permissionRequired;

    public AbstractCommand(String permission, boolean needPlayer, boolean permissionRequired) {
        this.permission = permission;
        this.needPlayer = needPlayer;
        this.permissionRequired = permissionRequired;
    }

    /**
     * Ce qui doit être exécuter quand le joueur fait la commande
     * @param sender L'envoyeur qui exécute la commande
     * @param args Les arguments que l'envoyeur a mis
     */
    protected abstract void onExecute(CommandSender sender, String[] args);

    /**
     * Vérifie si l'envoyeur est un joueur
     * @param sender L'envoyeur
     * @return Retourne vrai si c'est un joueur, faux si cela n'en est pas un
     */
    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * Permet d'envoyer un message au joueur avec le préfix
     * @param player Le joueur qui va recevoir le message
     * @param message Le message (pour mettre de la couleur, vous devez mettre le symbole & suivi du caractère correspondant à la couleur)
     */
    protected void sendMessageWithPrefix(Player player, String message) {
        sendMessageWithPrefix((CommandSender) player, message);
    }

    protected void sendMessageWithPrefix(CommandSender sender, String message) {
        sender.sendMessage(GameAPI.getInstance().getPrefix() + ChatUtility.format(message));
    }
    
}