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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginCommand extends AbstractCommand {

    private final GameAPI main;

    private final Map<UUID, Integer> playerPassword = new HashMap<>();

    public LoginCommand(GameAPI main) {
        super("gameapi:login", true, false);
        this.main = main;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (!main.getPlayersListener().getPlayers().contains(uuid) || main.getPlayersListener().getNewPlayers().contains(uuid)) {
            player.sendMessage(main.getPrefix() + "Vous ne pouvez pas faire cette commande.");
            return;
        }

        if (args.length == 0) {
            player.sendMessage(main.getPrefix() + "La commande est : /login <mot de passe>");
            return;
        }

        String encryptedPassword = main.getPlayerManager().getPlayerData(player).getEncryptedPassword();

        if (encryptedPassword.equals(encryptPassword(args[0]))) {
            playerPassword.remove(uuid);
            main.getPlayersListener().getPlayers().remove(uuid);
            main.getPlayerManager().sendPlayerToHub(player);
            Bukkit.broadcastMessage(ChatUtility.format("[&a+&r] ") + player.getName());
        }

        else {
            if (!playerPassword.containsKey(uuid)) {
                playerPassword.put(uuid, 1);
                player.sendMessage(main.getPrefix() + "Vous n'avez pas mis le bon mot de passe. (plus que 2 essais)");
                return;
            }

            int playerTry = playerPassword.get(uuid);
            if (playerTry == 1) {
                playerPassword.replace(uuid, playerTry + 1);
                player.sendMessage(main.getPrefix() + "Vous n'avez pas mis le bon mot de passe. (plus qu'1 essai)");
            } else if (playerTry == 2) {
                playerPassword.remove(uuid);
                player.kickPlayer("Mot de passe incorrect après 3 essais.");
            }

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