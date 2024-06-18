package fr.cel.eldenrpg.manager.npc;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.config.ConfigNPC;
import fr.cel.eldenrpg.manager.npc.npcs.Blacksmith;
import fr.cel.eldenrpg.manager.quest.Quest;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NPCManager {

    @Getter private final static Map<String, NPC> npcs = new HashMap<>();

    private final EldenRPG main;

    public NPCManager(EldenRPG main) {
        this.main = main;
        loadNPCs();
    }

    private void loadNPCs() {
        loadNPCsWithFile();

        Blacksmith blacksmith = new Blacksmith(main);
        registerNPC(blacksmith, main.getQuestManager().getQuestById("slime"));
    }

    /**
     * Permet d'enregistrer un nouveau npc
     * @param npc Le npc
     * @param quest La quête
     */
    private void registerNPC(NPC npc, Quest quest) {
        npc.setQuest(quest);
        npc.create();
        npc.showToAll();
        npcs.put(npc.getName(), npc);
    }

    /**
     * Permet de charger les NPCs contenus dans le dossier "npcs" du plugin
     */
    public void loadNPCsWithFile() {
        npcs.clear();

        File folder = new File(main.getDataFolder(), "npcs");
        if (!folder.exists()) folder.mkdirs();
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                String name = file.getName().replace(".yml", "");
                ConfigNPC config = new ConfigNPC(main, name);

                NPC npc = config.getNPC();
                npc.create();
                npcs.put(npc.getName(), npc);
                npc.showToAll();
            }
        }
    }

    /**
     * Permet de recharger les NPCs quand le serveur s'éteint
     */
    public void reloadNPCs() {
        removeToAll();
        loadNPCsWithFile();
    }

    /**
     * Permet de cacher tous les NPCs des joueurs
     */
    public static void removeToAll() {
        for (NPC npc : getNpcs().values()) {
            npc.hideToAll();
        }
    }

}