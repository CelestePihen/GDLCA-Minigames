package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WelcomeCommand extends AbstractCommand {

    private final PlayerManager playerManager;

    public WelcomeCommand(PlayerManager playerManager) {
        super("gameapi:welcome", true, false);
        this.playerManager = playerManager;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        final Player player = (Player) sender;

        if (playerManager.getPlayersWhoWelcomed().contains(player.getUniqueId())) {
            sendMessageWithPrefix(player, "Tu as déjà souhaité la bienvenue au nouveau joueur.");
            return;
        }

        if (playerManager.getNewPlayer() == null) {
            sendMessageWithPrefix(player, "Il n'y a pas eu de nouveau joueur pour l'instant.");
            return;
        }

        if (playerManager.getNewPlayer().equals(player.getUniqueId())) {
            sendMessageWithPrefix(player, "Vous ne pouvez vous souhaiter la bienvenue...");
            return;
        }

        Player newPlayer = Bukkit.getPlayer(playerManager.getNewPlayer());
        if (newPlayer == null) {
            sendMessageWithPrefix(player, "&cErreur avec le nouveau joueur.");
            return;
        }

        Bukkit.broadcastMessage(GameAPI.getPrefix() + player.getName() + " a souhaité la bienvenue à " + newPlayer.getName());
        playerManager.getPlayerData(player).addCoins(5.0D);
        playerManager.getPlayersWhoWelcomed().add(player.getUniqueId());
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}
