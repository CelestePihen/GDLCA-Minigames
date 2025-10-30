package fr.cel.hub.manager;

import fr.cel.gameapi.manager.npc.NPCManager;
import fr.cel.hub.manager.event.winter2025.SantaNPC;
import org.bukkit.plugin.java.JavaPlugin;

public class HubNPCManager extends NPCManager {

    public HubNPCManager(JavaPlugin main) {
        super(main);
    }

    @Override
    public void loadCustomNPCs() {
        SantaNPC santaNPC = new SantaNPC();
        this.npcs.put("santa", santaNPC);

        santaNPC.create();
        santaNPC.showToAll();
    }

}
