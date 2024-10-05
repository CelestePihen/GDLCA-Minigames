package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GameCompassComand extends AbstractCommand {

    private final GameAPI main;

    public GameCompassComand(GameAPI main) {
        super("gameapi:gamecompass", true, true);
        this.main = main;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        final Player player = (Player) sender;

        if (!main.getPlayerManager().containsPlayerInHub(player)) {
            player.sendMessage(GameAPI.getPrefix() + "Tu ne peux pas obtenir le Sélectionneur de mini-jeux en dehors du Hub.");
            return;
        }

        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS).setDisplayName("&rSélectionneur de mini-jeux").toItemStack());
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}