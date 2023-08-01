package fr.cel.valocraft.manager;

import fr.cel.hub.Hub;
import fr.cel.hub.manager.PlayerManager;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.valocraft.ValoCraft;
import lombok.Getter;

public class ValoGameManager {

    @Getter private static ValoGameManager gameManager;

    @Getter private final ValoCraft main;
    @Getter private final PlayerManager playerManager;
    @Getter private final String prefix = ChatUtility.format("&6[Valocraft]&r ");

    @Getter private ArenaManager arenaManager;

    public ValoGameManager(ValoCraft main) {
        this.main = main;
        gameManager = this;
        this.arenaManager = new ArenaManager(main, this);
        this.playerManager = Hub.getHub().getPlayerManager();
    }

    public void reloadArenaManager() {
        this.arenaManager = new ArenaManager(main, this);
    }

}