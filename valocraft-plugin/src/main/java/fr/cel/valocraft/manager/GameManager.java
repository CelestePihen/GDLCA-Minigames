package fr.cel.valocraft.manager;

import fr.cel.hub.Hub;
import fr.cel.hub.manager.PlayerManager;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.valocraft.ValoCraft;
import lombok.Getter;

public class GameManager {
    
    @Getter private final ValoCraft main;
    @Getter private ArenaManager arenaManager;
    @Getter private PlayerManager playerManager;
    @Getter private String prefix = ChatUtility.format("&6[Valocraft] &f");

    public GameManager(ValoCraft main) {
        this.main = main;
        this.arenaManager = new ArenaManager(main, this);
        this.playerManager = Hub.getHub().getPlayerManager();
    }

    public void reloadArenaManager() {
        this.arenaManager = new ArenaManager(main, this);
    }

}