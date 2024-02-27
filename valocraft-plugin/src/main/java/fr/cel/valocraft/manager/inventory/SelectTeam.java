package fr.cel.valocraft.manager.inventory;

import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.valocraft.manager.arena.state.pregame.StartingArenaState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SelectTeam extends AbstractInventory {

    public SelectTeam() {
        super("Sélecteur d'équipes", 9);
    }

    @Override
    protected void addItems(Inventory inventory) {
        inventory.setItem(3, new ItemBuilder(Material.BLUE_WOOL).setDisplayName("&1Équipe Bleue").toItemStack());
        inventory.setItem(4, new ItemBuilder(Material.WHITE_WOOL).setDisplayName("Spectateur").toItemStack());
        inventory.setItem(5, new ItemBuilder(Material.RED_WOOL).setDisplayName("&cÉquipe Rouge").toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        ValoArena arena = ValoCraft.getGameManager().getArenaManager().getArenaByPlayer(player);

        if (arena.getArenaState() instanceof StartingArenaState) {
            player.sendMessage(arena.getGameManager().getPrefix() + "Vous n'avez pas le droit de changer d'équipe quand la partie est lancée.");
            player.closeInventory();
        }

        switch (item.getType()) {
            case WHITE_WOOL -> {
                arena.getBlueTeam().removePlayer(player);
                arena.getRedTeam().removePlayer(player);
                arena.sendMessage(player.getDisplayName() + " est maintenant dans aucune équipe.");

                player.sendTitle(ChatUtility.format("Vous êtes maintenant dans aucune équipe."), "", 10, 70, 20);
                player.getInventory().getItemInMainHand().setType(Material.WHITE_WOOL);
                player.closeInventory();
            }

            case RED_WOOL -> {
                arena.getBlueTeam().removePlayer(player);
                arena.getRedTeam().addPlayer(player);
                arena.sendMessage(player.getDisplayName() + " a rejoint l'&céquipe rouge&r.");

                player.sendTitle(ChatUtility.format("Vous avez rejoint l'&céquipe rouge&r."), "", 10, 70, 20);
                player.getInventory().getItemInMainHand().setType(Material.RED_WOOL);
                player.closeInventory();
            }

            case BLUE_WOOL -> {
                arena.getRedTeam().removePlayer(player);
                arena.getBlueTeam().addPlayer(player);
                arena.sendMessage(player.getDisplayName() + " a rejoint l'&1équipe bleue&r.");

                player.sendTitle(ChatUtility.format("Vous avez rejoint l'&1équipe bleue&r."), "", 10, 70, 20);
                player.getInventory().getItemInMainHand().setType(Material.BLUE_WOOL);
                player.closeInventory();
            }

            default -> {}
        }
    }

}