package fr.cel.eldenrpg.listeners;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.npc.NPC;
import fr.cel.eldenrpg.manager.npc.NPCManager;
import fr.cel.eldenrpg.manager.player.ERPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;

public class NPCListener extends ERListener {

    public NPCListener(EldenRPG main) {
        super(main);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location loc = event.getTo();

        for (NPC npc : NPCManager.getNpcs().values()) {
            if (loc.getWorld() != npc.getLocation().getWorld()) continue;

            Location newLoc = loc.clone();
            newLoc.setDirection(newLoc.subtract(npc.getServerPlayer().getBukkitEntity().getLocation()).toVector());
            npc.lookAt(event.getPlayer(), newLoc);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        for (NPC npc : NPCManager.getNpcs().values()) {
            npc.spawn(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerUseUnknownEntity(PlayerUseUnknownEntityEvent event) {
        Player player = event.getPlayer();
        ERPlayer erPlayer = main.getPlayerManager().getPlayerData(player.getUniqueId());

        if (erPlayer == null) {
            sendMessage(player, "Erreur avec votre profil ! Merci de contacter un administrateur.");
            return;
        }

        if (event.getHand() == EquipmentSlot.HAND && event.isAttack()) {
            for (NPC npc : NPCManager.getNpcs().values()) {
                if (event.getEntityId() == npc.getServerPlayer().getId()) {
                    npc.interact(player, erPlayer);
                }
            }
        }
    }

}