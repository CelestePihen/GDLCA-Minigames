package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public abstract class AbstractCommand implements TabExecutor {

    private final String permission;
    private final boolean needPlayer;
    private final boolean permissionRequired;

    public AbstractCommand(String permission, boolean needPlayer, boolean permissionRequired) {
        this.permission = permission;
        this.needPlayer = needPlayer;
        this.permissionRequired = permissionRequired;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (needPlayer && !(sender instanceof Player)) {
            sendMessageWithPrefix(sender, Component.text("Tu dois etre un joueur pour effectuer cette commande."));
            return false;
        }

        else if (!sender.hasPermission(permission) && isPermissionRequired()) {
            sendMessageWithPrefix(sender, Component.text("Tu n'as pas la permission d'effectuer cette commande."));
            return false;
        }

        onExecute(sender, args);
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
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
    protected abstract void onExecute(@NotNull CommandSender sender, String @NotNull [] args);

    protected abstract List<String> onTabComplete(Player player, String[] args);

    /**
     * Vérifie si un joueur est en ligne
     * @param player Le joueur à vérifier
     * @param sender L'envoyer
     * @return Retourne true si le joueur est présent. Si non, alors envoie un message au sender et retourne false
     */
    @Contract("null, _ -> false; !null, _ -> true")
    protected boolean isPlayerOnline(@Nullable Player player, CommandSender sender) {
        if (player == null) {
            sendMessageWithPrefix(sender, Component.text("Le joueur n'existe pas ou n'est pas connecté."));
            return false;
        }
        return true;
    }

    /**
     * Permet d'envoyer un message à la personne qui a exécuté la commande avec le préfixe
     * @param sender L'envoyeur qui va recevoir le message
     * @param message Le message (pour mettre de la couleur, vous pouvez utiliser l'énumération disponible dans ChatUtility)
     */
    protected void sendMessageWithPrefix(CommandSender sender, Component message) {
        sender.sendMessage(GameAPI.getPrefix().append(message));
    }
    
}