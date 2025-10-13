package fr.cel.gameapi.command;

import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameCompassComand extends AbstractCommand {

    private final PlayerManager playerManager;

    public GameCompassComand(PlayerManager playerManager) {
        super("gameapi:gamecompass", true, true);
        this.playerManager = playerManager;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        final Player player = (Player) sender;

        if (!playerManager.containsPlayerInHub(player)) {
            sendMessageWithPrefix(player, Component.text("Tu ne peux pas obtenir le Sélectionneur de mini-jeux en dehors du Hub."));
            return;
        }

        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS)
                .itemName(Component.text("Sélectionneur de mini-jeux", NamedTextColor.WHITE))
                .toItemStack());

        player.getInventory().setItem(8, new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(player.getPlayerProfile())
                .itemName(Component.text("Mon Profil", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .toItemStack());
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}