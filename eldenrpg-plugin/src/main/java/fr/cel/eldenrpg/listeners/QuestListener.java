package fr.cel.eldenrpg.listeners;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.player.ERPlayer;
import fr.cel.eldenrpg.manager.player.PlayerManager;
import fr.cel.eldenrpg.manager.quest.Quest;
import fr.cel.eldenrpg.manager.quest.quests.KillQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class QuestListener implements Listener {

    private final EldenRPG main;

    public QuestListener(EldenRPG main) {
        this.main = main;
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;
        Player player = event.getEntity().getKiller();
        ERPlayer erPlayer = PlayerManager.getPlayerDataMap().get(player.getUniqueId());

        if (erPlayer != null) {
            for (Quest activeQuest : erPlayer.getActiveQuests()) {
                if (activeQuest instanceof KillQuest killQuest) {
                    if (killQuest.getTarget().equals(event.getEntity().getType())) {
                        killQuest.setProgress(killQuest.getProgress() + 1);

                        player.sendMessage("Tu as tué " + killQuest.getProgress() + " " + getMobs(killQuest.getProgress(), killQuest) + " sur " + killQuest.getAmount());

                        if (killQuest.getProgress() >= killQuest.getAmount()) {
                            erPlayer.activeToFinished(killQuest);
                            killQuest.setProgress(5);
                            player.sendMessage("Vous pouvez aller récupérer votre récompense !");
                            return;
                        }
                    }
                }
            }
        }
    }

    private String getMobs(int killedMobs, KillQuest killQuest) {
        return killedMobs == 1 ? killQuest.getTarget().name().toLowerCase() : killQuest.getTarget().name().toLowerCase() + "s";
    }

}