package fr.cel.gameapi.commands;

import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import fr.cel.gameapi.manager.cosmetic.CosmeticType;
import fr.cel.gameapi.manager.cosmetic.CosmeticsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to manage cosmetics
 * Usage: /cosmetics <open|give|remove|list>
 */
public class CosmeticsCommand extends AbstractCommand {

    private final CosmeticsManager cosmeticsManager;

    public CosmeticsCommand(CosmeticsManager cosmeticsManager) {
        super("gameapi:cosmetics", true, true);
        this.cosmeticsManager = cosmeticsManager;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return;

        if (args.length == 0) {
            openCosmeticsGUI(player);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "give" -> handleGive(player, args);
            case "remove" -> handleRemove(player, args);
            case "list" -> handleList(player);
            default -> player.sendMessage(Component.text("Usage: /cosmetics <give|remove|list>", NamedTextColor.RED));
        }
    }

    /**
     * Open the cosmetics GUI for a player
     */
    private void openCosmeticsGUI(Player player) {
        cosmeticsManager.openGUI(player);
    }

    /**
     * Give a cosmetic to a player
     * Usage: /cosmetics give <player> <cosmeticId>
     */
    private void handleGive(Player sender, String[] args) {
        if (!sender.hasPermission("cosmetics.admin")) {
            sender.sendMessage(Component.text("Vous n'avez pas la permission.", NamedTextColor.RED));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /cosmetics give <joueur> <cosmeticId>", NamedTextColor.RED));
            return;
        }

        Player target = sender.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Joueur introuvable.", NamedTextColor.RED));
            return;
        }

        String cosmeticId = args[2];
        if (cosmeticsManager.giveCosmetic(target, cosmeticId)) {
            sender.sendMessage(Component.text("Cosmétique donné à " + target.getName() + ".", NamedTextColor.GREEN));
            target.sendMessage(Component.text("Vous avez reçu un nouveau cosmétique !", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Erreur: cosmétique introuvable ou déjà possédé.", NamedTextColor.RED));
        }
    }

    /**
     * Remove a cosmetic from a player
     * Usage: /cosmetics remove <player> <cosmeticId>
     */
    private void handleRemove(Player sender, String[] args) {
        if (!sender.hasPermission("cosmetics.admin")) {
            sender.sendMessage(Component.text("Vous n'avez pas la permission.", NamedTextColor.RED));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /cosmetics remove <joueur> <cosmeticId>", NamedTextColor.RED));
            return;
        }

        Player target = sender.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Joueur introuvable.", NamedTextColor.RED));
            return;
        }

        String cosmeticId = args[2];
        if (cosmeticsManager.removeCosmetic(target, cosmeticId)) {
            sender.sendMessage(Component.text("Cosmétique retiré à " + target.getName() + ".", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Erreur: cosmétique introuvable ou non possédé.", NamedTextColor.RED));
        }
    }

    /**
     * List all cosmetics
     */
    private void handleList(Player sender) {
        sender.sendMessage(Component.text("=== Liste des cosmétiques ===", NamedTextColor.GOLD));

        for (CosmeticType type : CosmeticType.values()) {
            sender.sendMessage(Component.text(type.getDisplayName() + ":", NamedTextColor.YELLOW));

            List<Cosmetic> cosmetics = cosmeticsManager.getCosmeticsByType(type);
            if (cosmetics.isEmpty()) {
                sender.sendMessage(Component.text("  Aucun", NamedTextColor.GRAY));
            } else {
                for (Cosmetic cosmetic : cosmetics) {
                    sender.sendMessage(Component.text("  - " + cosmetic.getId() + " (" + cosmetic.getName() + ")", NamedTextColor.WHITE));
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull Player player, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (!player.isOp()) return completions;

        if (args.length == 1) {
            completions.add("give");
            completions.add("remove");
            completions.add("list");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("remove")) {
                return null; // Player names
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("remove")) {
                for (Cosmetic cosmetic : cosmeticsManager.getAllCosmetics()) {
                    completions.add(cosmetic.getId());
                }
            }
        }

        return completions;
    }
}

