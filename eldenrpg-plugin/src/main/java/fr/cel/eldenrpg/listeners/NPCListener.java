package fr.cel.eldenrpg.listeners;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.npc.NPC;
import fr.cel.eldenrpg.manager.npc.NPCManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class NPCListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location loc = event.getTo();

        for (NPC npc : NPCManager.getNpcs().values()) {
            if (loc.getWorld() != npc.getLocation().getWorld()) continue;
            if (npc.isDead()) return;

            Location newLoc = loc.clone();
            newLoc.setDirection(newLoc.subtract(npc.getNpc().getBukkitEntity().getLocation()).toVector());
            npc.lookAt(event.getPlayer(), newLoc);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        for (NPC npc : NPCManager.getNpcs().values()) {
            npc.spawn(event.getPlayer());
        }
    }

}