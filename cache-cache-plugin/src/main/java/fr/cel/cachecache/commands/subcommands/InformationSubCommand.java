package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.CCMapManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class InformationSubCommand implements SubCommand {

    private final GameManager gameManager;
    private final CCMapManager mapManager;

    public InformationSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
        this.mapManager = gameManager.getMain().getCcMapManager();
    }

    @Override
    public String getName() {
        return "information";
    }

    @Override
    public String getDescription() {
        return "Envoie les informations détaillées d'une map";
    }

    @Override
    public String getUsage() {
        return "/cc information <map>";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cachecache.admin");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci d'indiquer une carte.", NamedTextColor.RED)));
            return;
        }

        CCMap map = mapManager.getMaps().get(args[0]);
        if (map == null) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci de mettre une carte valide.", NamedTextColor.RED)));
            return;
        }

        String availableGroundItems = map.getAvailableGroundItems().stream().map(GroundItem::getName).collect(Collectors.joining(", "));
        String locationGroundItems = map.getLocationGroundItems().stream().map(loc -> "(" + loc.x() + ", " + loc.y() + ", " + loc.z() + ")").collect(Collectors.joining(", "));

        sender.sendMessage(gameManager.getPrefix()
                .append(Component.text("Map " + map.getDisplayName() + " (" + map.getMapName() + ")", NamedTextColor.YELLOW))
                .appendNewline()
                .append(Component.text("Mode : " + map.getCcMode().getName(), NamedTextColor.YELLOW))
                .appendNewline()
                .append(Component.text("Spawn Loc : (" + map.getSpawnLoc().x() + ", " + map.getSpawnLoc().y() + ", " + map.getSpawnLoc().z() + ")", NamedTextColor.YELLOW))
                .appendNewline()
                .append(Component.text("Waiting Loc : (" + map.getWaitingLoc().x() + ", " + map.getWaitingLoc().y() + ", " + map.getWaitingLoc().z() + ")", NamedTextColor.YELLOW))
                .appendNewline()
                .append(Component.text("Fall Damage : " + map.isFallDamage(), NamedTextColor.YELLOW))
                .appendNewline()
                .append(Component.text("Best Player : " + map.getBestPlayer() + " - Best Timer : " + map.getBestTimer(), NamedTextColor.YELLOW))
                .appendNewline()
                .append(Component.text("Available Ground Items : " + availableGroundItems, NamedTextColor.YELLOW))
                .appendNewline()
                .append(Component.text("Location Ground Items : " + locationGroundItems, NamedTextColor.YELLOW))
        );
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) return mapManager.getMaps().keySet().stream().toList();
        return List.of();
    }
}