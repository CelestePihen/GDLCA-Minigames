package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.manager.command.SubCommand;
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

public class CalculDepositSubCommand implements SubCommand {

    private final GameManager gameManager;

    public CalculDepositSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "calculdeposits";
    }

    @Override
    public String getDescription() {
        return "(Re-)Calcule les emplacements des dépôts de cadeaux (Événements Hiver 2025).";
    }

    @Override
    public String getUsage() {
        return "/cc calculdeposits <mapName> <x1> <y1> <z1> <x2> <y2> <z2>";
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

        if (args.length == 1) {
            CCMap map = gameManager.getMain().getCcMapManager().getMaps().get(args[0].toLowerCase());
            if (map == null) {
                player.sendMessage(gameManager.getPrefix().append(Component.text("La map '" + args[0] + "' n'existe pas.", NamedTextColor.RED)));
                return;
            }

            player.sendMessage(Component.text("Le nombre d'emplacements de dépôts de cadeaux dans la map '" + args[0] + "' est de "
                    + map.getWinterUtility().getChrismasTreeDepositLocations().size() + ".", NamedTextColor.GREEN));
            return;
        }

        if (args.length != 7) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Usage: /cc calculdeposits <mapName> <x1> <y1> <z1> <x2> <y2> <z2>", NamedTextColor.RED)));
            return;
        }

        CCMap map = gameManager.getMain().getCcMapManager().getMaps().get(args[0].toLowerCase());
        if (map == null) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("La map '" + args[0] + "' n'existe pas.", NamedTextColor.RED)));
            return;
        }

        int x1, y1, z1, x2, y2, z2;
        try {
            x1 = Integer.parseInt(args[1]);
            y1 = Integer.parseInt(args[2]);
            z1 = Integer.parseInt(args[3]);
            x2 = Integer.parseInt(args[4]);
            y2 = Integer.parseInt(args[5]);
            z2 = Integer.parseInt(args[6]);
        } catch (NumberFormatException ignored) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Merci de rentrer des entiers valides.", NamedTextColor.RED)));
            return;
        }

        // Assurer que x1 <= x2, y1 <= y2, z1 <= z2
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        World world = player.getWorld();

        List<String> deposits = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.REINFORCED_DEEPSLATE) {
                        deposits.add(x + "," + (y + 1) + "," + z);
                    }
                }
            }
        }

        map.getMapConfig().setValue("christmasTreeDeposits", deposits);
        map.getWinterUtility().setChrismasTreeDepositLocations(map.getMapConfig().getChristmasTreeDepositLocations());

        player.sendMessage(Component.text(deposits.size() + " emplacements de dépôts de cadeaux trouvés et sauvegardés !", NamedTextColor.GREEN));
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) return gameManager.getMain().getCcMapManager().getMaps().keySet().stream().toList();
        return List.of();
    }

}