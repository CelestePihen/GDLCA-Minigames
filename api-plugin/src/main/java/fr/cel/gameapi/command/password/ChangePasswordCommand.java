package fr.cel.gameapi.command.password;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ChangePasswordCommand extends AbstractCommand {

    private final GameAPI main;

    public ChangePasswordCommand(GameAPI main) {
        super("gameapi:changepsw", true, false);
        this.main = main;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0 || args.length == 1 || args.length == 2) {
            player.sendMessage(main.getPrefix() + "La commande est : /changepsw <ancien mdp> <nouveau mdp> <nouveau mdp>");
            return;
        }

        String actualPassword = main.getPlayerManager().getPlayerData(player).getEncryptedPassword();
        if (!actualPassword.equals(encryptPassword(args[0]))) {
            player.sendMessage(main.getPrefix() + "Votre ancien mot de passe est incorrect.");
            return;
        }

        if (!args[1].equals(args[2])) {
            player.sendMessage(main.getPrefix() + "Vous n'avez pas mis les 2 mêmes mots de passe.");
            return;
        }

        main.getPlayerManager().getPlayerData(player).setPassword(encryptPassword(args[2]));
        player.kickPlayer("Merci de vous reconnecter avec votre mot de passe.");
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