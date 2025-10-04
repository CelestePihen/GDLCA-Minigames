package fr.cel.cachecache.manager.groundItems;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.StatisticsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class InvisibilityItem extends GroundItem {

    private static final List<Component> LORES = List.of(
            Component.text("Cet objet vous permet de vous rendre invisible vous et vos alliés proches pendant quelques secondes."));

    public InvisibilityItem() {
        super("invisibilityItem", Material.GOLDEN_CARROT, Component.text("Invisibilité"), LORES, "cc_invisibility");
    }

    /**
     * @author Deadsky
     */
    @Override
    public void onInteract(Player player, CCMap map) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*7, 1, false, false));

        for (Entity entityNear : player.getNearbyEntities(12, 12, 12)) {
            if (entityNear instanceof Player pl) {
                if (map.getHiders().contains(player.getUniqueId()) && map.getHiders().contains(pl.getUniqueId())) {
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*4, 1, false, false));
                }
                else if (map.getSeekers().contains(player.getUniqueId()) && map.getSeekers().contains(pl.getUniqueId())) {
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*4, 1, false, false));
                }
            }
        }

        GameAPI.getInstance().getStatisticsManager().updatePlayerStatistic(player, StatisticsManager.PlayerStatistics.CC_INVISIBILITY_USAGE, 1);
        super.onInteract(player, map);
    }

}