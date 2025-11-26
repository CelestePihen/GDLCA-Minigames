package fr.cel.hub.commands;

import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.hub.manager.event.winter2025.HeadInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeadCommand extends AbstractCommand {

    public HeadCommand() {
        super("hub:head", true, false);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;
        new HeadInventory(player, false).open(player);
    }

    @Override
    protected List<String> onTabComplete(Player player, @NotNull String[] strings) {
        return List.of();
    }

}