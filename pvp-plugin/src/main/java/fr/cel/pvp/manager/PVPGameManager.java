package fr.cel.pvp.manager;

import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.pvp.PVP;
import fr.cel.pvp.manager.arena.ArenaManager;
import lombok.Getter;

public class PVPGameManager {
    
    @Getter private final PVP main;
    @Getter private ArenaManager arenaManager;
    @Getter private final String prefix = ChatUtility.format("&6[PVP] &f");

    @Getter private static PVPGameManager gameManager;

    public PVPGameManager(PVP main) {
        gameManager = this;
        this.main = main;
        this.arenaManager = new ArenaManager(main);
    }

    public void reloadArenaManager() {
        this.arenaManager = new ArenaManager(main);
    }

}