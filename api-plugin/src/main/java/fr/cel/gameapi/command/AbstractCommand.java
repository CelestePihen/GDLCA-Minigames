package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    private final String permission;
    private final boolean needPlayer;
    private final boolean permissionRequired;

    public AbstractCommand(String permission, boolean needPlayer, boolean permissionRequired) {
        this.permission = permission;
        this.needPlayer = needPlayer;
        this.permissionRequired = permissionRequired;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (needPlayer && !(sender instanceof Player)) {
            sender.sendMessage(GameAPI.getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
            return false;
        }

        else if (!sender.hasPermission(permission) && isPermissionRequired()) {
            sender.sendMessage(GameAPI.getPrefix() + "Vous n'avez pas la permission d'effectuer cette commande.");
            return false;
        }

        onExecute(sender, args);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (needPlayer && !(sender instanceof Player)) {
            return null;
        } else if (!sender.hasPermission(permission) && isPermissionRequired()) {
            return null;
        }

        return onTabComplete((Player) sender, args);
    }

    /**
     * Ce qui doit être exécuter quand le joueur fait la commande
     * @param sender L'envoyeur qui exécute la commande
     * @param args Les arguments que l'envoyeur a mis
     */
    protected abstract void onExecute(CommandSender sender, String[] args);

    protected abstract List<String> onTabComplete(Player player, String[] args);

    /**
     * Vérifie si un joueur est en ligne
     * @param player Le joueur à vérifier
     * @param sender L'envoyer
     * @return Retourne true si le joueur est présent. Si non, alors envoie un message au sender et retourne false
     */
    protected boolean isPlayerOnline(Player player, CommandSender sender) {
        if (player == null) {
            sendMessageWithPrefix(sender, "Le joueur n'existe pas ou n'est pas connecté.");
            return false;
        }
        return true;
    }

    /**
     * Permet d'envoyer un message à la personne qui a exécuté la commande avec le préfixe
     * @param sender L'envoyeur qui va recevoir le message
     * @param message Le message (pour mettre de la couleur, vous pouvez utiliser l'énumération disponible dans ChatUtility)
     */
    protected void sendMessageWithPrefix(CommandSender sender, String message) {
        sendMessage(sender, message, true);
    }

    /**
     * Permet d'envoyer un message à la personne qui a exécuté la commande
     * @param sender L'envoyeur qui va recevoir le message
     * @param message Le message (pour mettre de la couleur, vous pouvez utiliser l'énumération disponible dans ChatUtility)
     * @param withPrefix Permet de préciser si l'on veut le préfixe ou pas
     */
    protected void sendMessage(CommandSender sender, String message, boolean withPrefix) {
        StringBuilder stringBuilder = new StringBuilder();

        if (withPrefix) {
            stringBuilder.append(GameAPI.getPrefix());
        }

        stringBuilder.append(message);
        sender.sendMessage(ChatUtility.format(stringBuilder.toString()));
    }
    
}