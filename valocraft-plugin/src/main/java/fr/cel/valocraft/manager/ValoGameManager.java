package fr.cel.valocraft.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.valocraft.ValoCraft;
import lombok.Getter;

public class ValoGameManager {

    @Getter
    private static ValoGameManager gameManager;

    @Getter private final ValoCraft main;
    @Getter private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();
    @Getter private final String prefix = ChatUtility.format("&6[Valocraft]&r ");

    @Getter private ArenaManager arenaManager;

    public ValoGameManager(ValoCraft main) {
        gameManager = this;
        this.main = main;
        this.arenaManager = new ArenaManager(main, this);
    }

    public void reloadArenaManager() {
        this.arenaManager = new ArenaManager(main, this);
    }

}