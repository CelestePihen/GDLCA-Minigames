package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.GameManager;
import fr.cel.gameapi.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CalculSubCommand implements SubCommand {

    private final GameManager gameManager;

    public CalculSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "calcul";
    }

    @Override
    public String getDescription() {
        return "(Re-)Calcule les lampes de redstone (normalement) de la map Bunker.";
    }

    @Override
    public String getUsage() {
        return "/cc calcul x1 y1 z1 x2 y2 z2";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cachecache.admin");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return;

        if (args.length < 6) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Usage : /cc calcul x1 y1 z1 x2 y2 z2", NamedTextColor.RED)));
            return;
        }

        try {
            int x1 = Integer.parseInt(args[0]);
            int y1 = Integer.parseInt(args[1]);
            int z1 = Integer.parseInt(args[2]);
            int x2 = Integer.parseInt(args[3]);
            int y2 = Integer.parseInt(args[4]);
            int z2 = Integer.parseInt(args[5]);

            World world = player.getWorld();
            Map<Material, Integer> blocks = new HashMap<>();
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                        Block block = world.getBlockAt(x, y, z);
                        blocks.put(block.getType(), blocks.getOrDefault(block.getType(), 0) + 1);
                    }
                }
            }
            player.sendMessage(gameManager.getPrefix().append(Component.text("Calcul terminé !", NamedTextColor.GREEN)));
        } catch (NumberFormatException ignored) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Merci de rentrer des entiers valides.", NamedTextColor.RED)));
        }
    }

}