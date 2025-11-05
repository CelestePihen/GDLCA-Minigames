package fr.cel.gameapi.commands;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.StatisticsInventory;
import fr.cel.gameapi.manager.command.AbstractCommand;
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
            sendMessageWithPrefix(player, Component.text("Vos statistiques sont affichées."));
            GameAPI.getInstance().getInventoryManager().openInventory(new StatisticsInventory(player), player);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (isPlayerOnline(target, sender))
            GameAPI.getInstance().getInventoryManager().openInventory(new StatisticsInventory(target), player);
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return List.of();
    }

}