package fr.cel.hub.commands;

import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.hub.Hub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddHeadCommand extends AbstractCommand {

    private final Hub main;

    public AddHeadCommand(Hub main) {
        super("hub:addhead", true, true);
        this.main = main;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;

        int headId;
        if (args.length < 1) {
            headId = main.getHeadManager().getNextAvailableId();
        } else {
            try {
                headId = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sendMessageWithPrefix(player, Component.text("L'ID doit être un nombre valide.", NamedTextColor.RED));
                return;
            }
        }

        Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null || targetBlock.getType().isAir()) {
            sendMessageWithPrefix(player, Component.text("Vous ne regardez aucun bloc !", NamedTextColor.RED));
            return;
        }

        Location location = targetBlock.getLocation();

        boolean success = main.getHeadManager().addHead(location, headId);

        if (success) {
            sendMessageWithPrefix(player, Component.text("Tête #" + headId + " ajoutée aux coordonnées : ", NamedTextColor.GREEN)
                    .append(Component.text("X=" + location.getBlockX() + " Y=" + location.getBlockY() + " Z=" + location.getBlockZ(), NamedTextColor.YELLOW)));
        } else {
            sendMessageWithPrefix(player, Component.text("Erreur lors de l'ajout de la tête.", NamedTextColor.RED));
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, @NotNull String[] args) {
        return List.of();
    }

}