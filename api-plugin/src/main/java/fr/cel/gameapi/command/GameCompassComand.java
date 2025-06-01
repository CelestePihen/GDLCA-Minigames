package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GameCompassComand extends AbstractCommand {

    private final PlayerManager playerManager;

    public GameCompassComand(PlayerManager playerManager) {
        super("gameapi:gamecompass", true, true);
        this.playerManager = playerManager;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        final Player player = (Player) sender;

        if (!playerManager.containsPlayerInHub(player)) {
            player.sendMessage(GameAPI.getPrefix() + "Tu ne peux pas obtenir le Sélectionneur de mini-jeux en dehors du Hub.");
            return;
        }

        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS).setDisplayName("&rSélectionneur de mini-jeux").toItemStack());
        player.getInventory().setItem(8, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player.getPlayerProfile()).setDisplayName("&fMon Profil").toItemStack());
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}