package fr.cel.dailyquests.command;

import fr.cel.dailyquests.manager.BuildingManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BuildingBookCommand implements CommandExecutor {

    private final BuildingManager buildingManager;

    public BuildingBookCommand(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Vous ne pouvez pas faire cette commande."));
            return false;
        }

        buildingManager.giveBook(player);
        return true;
    }

}
