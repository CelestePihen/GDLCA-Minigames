package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.StatisticsInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StatisticsCommand extends AbstractCommand {

    public StatisticsCommand() {
        super("gameapi:statistics", true, false);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            GameAPI.getInstance().getInventoryManager().openInventory(new StatisticsInventory(player), player);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sendMessageWithPrefix(sender, Component.text("Le joueur n'existe pas ou n'est pas connecté."));
        } else {
            GameAPI.getInstance().getInventoryManager().openInventory(new StatisticsInventory(target), player);
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}