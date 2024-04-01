package fr.cel.gameapi.command.password;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class RegisterCommand extends AbstractCommand {

    private final GameAPI main;

    public RegisterCommand(GameAPI main) {
        super("gameapi:register", true, false);
        this.main = main;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (main.getDatabase().hasAccount(player)) {
            player.sendMessage(main.getPrefix() + "La commande pour changer de mot de passe est : /changepsw <ancien mdp> <nouveau mdp> <nouveau mdp>");
            return;
        }

        if (args.length == 0 || args.length == 1) {
            player.sendMessage(main.getPrefix() + "La commande est : /register <mot de passe> <mot de passe>");
            return;
        }

        if (args[0].equals(args[1])) {
            main.getDatabase().createAccount(player, encryptPassword(args[0]));
            main.getPlayersListener().getNewPlayers().remove(uuid);
            main.getPlayerManager().sendPlayerToHub(player);
            Bukkit.broadcastMessage(ChatUtility.format("[&a+&r] ") + player.getName());
        } else {
            player.sendMessage(main.getPrefix() + "Les 2 mots de passe ne sont pas les mêmes.");
        }

    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}