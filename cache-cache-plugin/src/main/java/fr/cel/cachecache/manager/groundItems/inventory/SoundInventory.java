package fr.cel.cachecache.manager.groundItems.inventory;

import fr.cel.cachecache.manager.groundItems.tasks.SoundCatTask;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.database.StatisticsManager;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class SoundInventory extends AbstractInventory {

    private final CCMap map;

    private static final List<Sound> GOAT_HORN_SOUNDS = Arrays.asList(Sound.ITEM_GOAT_HORN_SOUND_4, Sound.ITEM_GOAT_HORN_SOUND_7);

    public SoundInventory(CCMap map) {
        super(Component.text("Sons"), 9);
        this.map = map;
    }

    @Override
    protected void addItems(Inventory inventory) {
        ItemStack goat_horn = new ItemBuilder(Material.GOAT_HORN).itemName(Component.text("Corne de chèvres")).toItemStack();
        ItemStack cat = new ItemBuilder(Material.STRING).itemName(Component.text("Chats")).toItemStack();
        inventory.addItem(goat_horn, cat);
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (!map.isPlayerInMap(player)) return;

        if (item.getType() == Material.STRING) {
            if (map.getMapName().equalsIgnoreCase("moulin") && map.getCheckAdvancements().getMiaou().getPlayerInside().get(player.getUniqueId())) {
                map.getCheckAdvancements().giveMiaou(player);
            }

            SoundCatTask soundCatTask = new SoundCatTask(map);
            soundCatTask.runTaskTimer(map.getGameManager().getMain(), 0, 20);
            map.addItemTask(soundCatTask);

            removeItem(player);
            GameAPI.getInstance().getStatisticsManager().updatePlayerStatistic(player, StatisticsManager.PlayerStatistics.CC_SOUND_USAGE, 1);
        }

        else if (item.getType() == Material.GOAT_HORN) {
            Sound sound = GOAT_HORN_SOUNDS.get(ThreadLocalRandom.current().nextInt(GOAT_HORN_SOUNDS.size()));
            for (UUID uuid : map.getPlayers()) {
                Player pl = Bukkit.getPlayer(uuid);
                if (pl == null || pl.getGameMode() == GameMode.SPECTATOR) continue;
                pl.playSound(pl, sound, SoundCategory.AMBIENT, 2.0f, 1.0f);
            }

            if (map.getMapName().equalsIgnoreCase("sp") || map.getMapName().equalsIgnoreCase("sp2")) {
                map.getCheckAdvancements().giveRaidChateau(player);
            }

            removeItem(player);
            GameAPI.getInstance().getStatisticsManager().updatePlayerStatistic(player, StatisticsManager.PlayerStatistics.CC_SOUND_USAGE, 1);
        }
    }

    private void removeItem(Player player) {
        player.closeInventory();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        itemInHand.setAmount(itemInHand.getAmount() - 1);
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}