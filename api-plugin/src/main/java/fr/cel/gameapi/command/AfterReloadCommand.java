package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AfterReloadCommand extends AbstractCommand {

    private final GameAPI main;

    public AfterReloadCommand(GameAPI main) {
        super("gameapi:afterrl", false, true);
        this.main = main;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            main.getPlayerManager().addPlayerData(pl);
            main.getPlayerManager().sendPlayerToHub(pl);
        }
    }

}