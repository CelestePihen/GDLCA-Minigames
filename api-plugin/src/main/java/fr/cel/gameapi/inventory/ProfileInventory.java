package fr.cel.gameapi.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.PlayerData;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProfileInventory extends AbstractInventory {

    private final Player player;
    private final PlayerData playerData;

    public ProfileInventory(Player player) {
        super("Mon Profil", 27);
        this.player = player;
        this.playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player.getPlayerProfile()).setDisplayName(player.getName())
                .addLoreLine(ChatUtility.format("C'est vous.", ChatUtility.RED)).toItemStack());

        inv.setItem(10, new ItemBuilder(Material.GOLD_INGOT).setDisplayName(ChatUtility.YELLOW + "Pièces")
                .setLore(ChatUtility.format(playerData.getCoins() + " pièces", ChatUtility.GOLD)).toItemStack());

        inv.setItem(12, new ItemBuilder(Material.CANDLE).setDisplayName(ChatUtility.GRAY + "Amis").toItemStack());

        inv.setItem(14, new ItemBuilder(Material.ENDER_PEARL).setDisplayName(ChatUtility.AQUA + "Partie").
                setLore(ChatUtility.format("Bientôt", ChatUtility.GOLD)).toItemStack());

        inv.setItem(16, new ItemBuilder(Material.CLOCK).setDisplayName(ChatUtility.BLUE + "Options").toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case PLAYER_HEAD -> player.sendMessage(GameAPI.getPrefix() + ChatUtility.format("C'est vous.", ChatUtility.RED));

            case GOLD_INGOT -> {
                double coins = playerData.getCoins();
                if (coins < 2) {
                    player.sendMessage(GameAPI.getPrefix() + "Vous avez " + playerData.getCoins() + " pièce.");
                } else {
                    player.sendMessage(GameAPI.getPrefix() + "Vous avez " + playerData.getCoins() + " pièces.");
                }
            }

            case CANDLE -> GameAPI.getInstance().getInventoryManager().openInventory(new FriendsInventory(player), player);

            case ENDER_PEARL -> player.sendMessage(GameAPI.getPrefix() + "Bientôt");

            case CLOCK -> GameAPI.getInstance().getInventoryManager().openInventory(new OptionsInventory(player), player);
        }
    }

}