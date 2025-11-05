package fr.cel.decorationsplugin.command;

import fr.cel.decorationsplugin.DecorationsPlugin;
import fr.cel.decorationsplugin.inventory.DecorationInventory;
import fr.cel.decorationsplugin.manager.DecorationsManager;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DecorationsCommand extends AbstractCommand {

    private final DecorationsManager manager;

    public DecorationsCommand(DecorationsManager manager) {
        super("decorations:decorations", true, true);
        this.manager = manager;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            GameAPI.getInstance().getInventoryManager().openInventory(new DecorationInventory(manager), player);
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            manager.loadAll();
            player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Les décorations ont été rechargées avec succès.")));
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String @NotNull [] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return null;
    }

}