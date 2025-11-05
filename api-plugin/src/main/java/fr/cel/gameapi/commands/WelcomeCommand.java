package fr.cel.gameapi.commands;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WelcomeCommand extends AbstractCommand {

    private final PlayerManager playerManager;

    public WelcomeCommand(PlayerManager playerManager) {
        super("gameapi:welcome", true, false);
        this.playerManager = playerManager;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        final Player player = (Player) sender;

        if (playerManager.getPlayersWhoWelcomed().contains(player.getUniqueId())) {
            sendMessageWithPrefix(player, Component.text("Tu as déjà souhaité la bienvenue au nouveau joueur."));
            return;
        }

        if (playerManager.getNewPlayer() == null) {
            sendMessageWithPrefix(player, Component.text("Il n'y a pas eu de nouveau joueur pour l'instant."));
            return;
        }

        if (playerManager.getNewPlayer().equals(player.getUniqueId())) {
            sendMessageWithPrefix(player, Component.text("Tu ne peux pas te souhaiter la bienvenue..."));
            return;
        }

        Player newPlayer = Bukkit.getPlayer(playerManager.getNewPlayer());
        if (newPlayer == null) {
            sendMessageWithPrefix(player, Component.text("Erreur avec le nouveau joueur.", NamedTextColor.RED));
            return;
        }

        newPlayer.sendMessage(GameAPI.getPrefix().append(Component.text(player.getName())).append(Component.text(" a souhaité la bienvenue à ")).append(Component.text(newPlayer.getName())));
        playerManager.getPlayerData(player).addCoins(5.0D);
        playerManager.getPlayersWhoWelcomed().add(player.getUniqueId());
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return List.of();
    }

}
