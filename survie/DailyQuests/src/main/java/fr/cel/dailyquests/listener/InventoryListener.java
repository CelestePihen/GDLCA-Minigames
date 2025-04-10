package fr.cel.dailyquests.listener;

import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public final class InventoryListener implements Listener {

    private final QuestManager manager;

    public InventoryListener(QuestManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST) return;

        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase("Vos quêtes")) {
            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player player)) return;

            QPlayer qPlayer = manager.getPlayerData().get(player.getUniqueId());
            if (qPlayer == null) return;

            if (event.getSlot() == 11) {
                if (qPlayer.isHasRenewDaily()) {
                    qPlayer.renewQuest(Quest.DurationType.DAILY, manager, true);
                } else {
                    player.sendMessage(Component.text("Vous avez déjà utilisé votre renouvellement journalier."));
                }
            }
            else if (event.getSlot() == 13) {
                if (qPlayer.isHasRenewWeekly()) {
                    qPlayer.renewQuest(Quest.DurationType.WEEKLY, manager, true);
                } else {
                    player.sendMessage(Component.text("Vous avez déjà utilisé votre renouvellement hebdomadaire."));
                }
            }
        }
    }

}