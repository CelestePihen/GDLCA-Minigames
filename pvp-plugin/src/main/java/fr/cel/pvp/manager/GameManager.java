package fr.cel.pvp.manager;

import fr.cel.hub.Hub;
import fr.cel.hub.manager.PlayerManager;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.pvp.PVP;
import fr.cel.pvp.manager.arena.ArenaManager;
import lombok.Getter;

public class GameManager {
    
    @Getter private final PVP main;
    @Getter private ArenaManager arenaManager;
    @Getter private PlayerManager playerManager;
    @Getter private String prefix = ChatUtility.format("&6[PVP] &f");

    @Getter private static GameManager gameManager;

    public GameManager(PVP main) {
        gameManager = this;
        this.main = main;
        this.arenaManager = new ArenaManager(main);
        this.playerManager = Hub.getHub().getPlayerManager();
    }

    public void reloadArenaManager() {
        this.arenaManager = new ArenaManager(main);
    }

}