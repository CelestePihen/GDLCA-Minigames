package fr.cel.cachecache.manager.groundItems.inventory;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.manager.groundItems.tasks.SoundCatTimer;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.database.StatisticsManager;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SoundInventory extends AbstractInventory {

    private final CCArena arena;

    private final List<Sound> goatHornSounds = Arrays.asList(Sound.ITEM_GOAT_HORN_SOUND_4, Sound.ITEM_GOAT_HORN_SOUND_7);

    public SoundInventory(CCArena arena) {
        super("Sons", 9);
        this.arena = arena;
    }

    @Override
    protected void addItems(Inventory inventory) {
        ItemStack goat_horn = new ItemBuilder(Material.GOAT_HORN).setItemName("Corne de chèvres").toItemStack();
        ItemStack cat = new ItemBuilder(Material.STRING).setItemName("Chats").toItemStack();
        inventory.addItem(goat_horn, cat);
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (!arena.isPlayerInArena(player)) return;

        if (item.getType() == Material.STRING) {
            removeItem(player);

            if (arena.getCheckAdvancements().getMiaou().getPlayerInside().get(player.getUniqueId())) {
                arena.getCheckAdvancements().giveMiaou(player);
            }

            GameAPI.getInstance().getStatisticsManager().updatePlayerStatistic(player, StatisticsManager.PlayerStatistics.CC_SOUND_USAGE, 1);
            new SoundCatTimer(arena).runTaskTimer(arena.getGameManager().getMain(), 0, 20);
        }

        else if (item.getType() == Material.GOAT_HORN) {
            removeItem(player);

            Sound sound = goatHornSounds.get(new Random().nextInt(goatHornSounds.size()));
            for (UUID uuid : arena.getPlayers()) {
                Player pl = Bukkit.getPlayer(uuid);
                if (pl == null || pl.getGameMode() == GameMode.SPECTATOR) continue;
                pl.playSound(pl, sound, SoundCategory.AMBIENT, 2.0f, 1.0f);
            }

            if (arena.getArenaName().equalsIgnoreCase("sp") || arena.getArenaName().equalsIgnoreCase("sp2")) {
                arena.getCheckAdvancements().giveRaidChateau(player);
            }

            GameAPI.getInstance().getStatisticsManager().updatePlayerStatistic(player, StatisticsManager.PlayerStatistics.CC_SOUND_USAGE, 1);
        }
    }

    private void removeItem(Player player) {
        player.closeInventory();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() == 1) player.getInventory().setItemInMainHand(null);
        else itemInHand.setAmount(itemInHand.getAmount() - 1);
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}