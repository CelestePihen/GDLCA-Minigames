package fr.cel.valocraft.inventory;

import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.pregame.StartingArenaState;
import fr.cel.valocraft.manager.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SelectTeam extends AbstractInventory {

    private static final ItemStack WHITE_WOOL = new ItemBuilder(Material.WHITE_WOOL).itemName(Component.text("Spectateur")).toItemStack();
    private static final ItemStack BLUE_WOOL = new ItemBuilder(Material.BLUE_WOOL).itemName(Component.text("Équipe Bleue", NamedTextColor.BLUE)).toItemStack();
    private static final ItemStack RED_WOOL = new ItemBuilder(Material.RED_WOOL).itemName(Component.text("Équipe Rouge", NamedTextColor.RED)).toItemStack();

    private final GameManager gameManager;

    public SelectTeam(GameManager gameManager) {
        super(Component.text("Sélecteur d'équipes"), 9);
        this.gameManager = gameManager;
    }

    @Override
    protected void addItems(Inventory inventory) {
        inventory.setItem(3, BLUE_WOOL);
        inventory.setItem(4, WHITE_WOOL);
        inventory.setItem(5, RED_WOOL);
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        ValoArena arena = gameManager.getMain().getValoArenaManager().getArenaByPlayer(player);

        if (arena.getArenaState() instanceof StartingArenaState) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Vous n'avez pas le droit de changer d'équipe quand la partie est lancée.")));
            player.closeInventory();
        }

        switch (item.getType()) {
            case WHITE_WOOL -> {
                arena.getBlueTeam().removePlayer(player);
                arena.getRedTeam().removePlayer(player);
                arena.sendMessage(player.displayName().append(Component.text(" est maintenant spectateur.")));

                player.showTitle(Title.title(Component.text("Vous êtes maintenant spectateur."), Component.empty()));
                player.getInventory().setItemInMainHand(WHITE_WOOL);
                player.closeInventory();
            }

            case RED_WOOL -> {
                arena.getBlueTeam().removePlayer(player);
                arena.getRedTeam().addPlayer(player);
                arena.sendMessage(player.displayName().append(Component.text(" a rejoint l'")).append(Component.text("équipe rouge", NamedTextColor.RED)));

                player.showTitle(Title.title(Component.text("Vous avez rejoint l'").append(Component.text("équipe rouge", NamedTextColor.RED)), Component.empty()));
                player.getInventory().setItemInMainHand(RED_WOOL);
                player.closeInventory();
            }

            case BLUE_WOOL -> {
                arena.getRedTeam().removePlayer(player);
                arena.getBlueTeam().addPlayer(player);
                arena.sendMessage(player.displayName().append(Component.text(" a rejoint l'")).append(Component.text("équipe bleue", NamedTextColor.BLUE)));

                player.showTitle(Title.title(Component.text("Vous avez rejoint l'").append(Component.text("équipe bleue", NamedTextColor.BLUE)), Component.empty()));
                player.getInventory().setItemInMainHand(BLUE_WOOL);
                player.closeInventory();
            }

            default -> {}
        }
    }

}