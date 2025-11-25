package fr.cel.hub.commands;

import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.hub.Hub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CalculHeadsCommand extends AbstractCommand {

    private final Hub main;

    public CalculHeadsCommand(Hub main) {
        super("hub:calculheads", false, true);
        this.main = main;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        sendMessageWithPrefix(sender, Component.text("Le nombre d'emplacements des têtes est "
                    + main.getHeadManager().getHeadLocations().size() + ".", NamedTextColor.GREEN));
    }

    @Override
    protected List<String> onTabComplete(Player player, @NotNull String[] strings) {
        return List.of();
    }
}