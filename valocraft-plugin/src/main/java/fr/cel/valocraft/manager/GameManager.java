package fr.cel.valocraft.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.valocraft.ValoCraft;
import lombok.Getter;

public class GameManager {

    @Getter private static GameManager gameManager;

    @Getter private final ValoCraft main;
    @Getter private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();
    @Getter private final String prefix = ChatUtility.format("&6[Valocraft]&r ");

    @Getter private ValoArenaManager valoArenaManager;

    public GameManager(ValoCraft main) {
        gameManager = this;
        this.main = main;
        this.valoArenaManager = new ValoArenaManager(main, this);
    }

    public void reloadArenaManager() {
        this.valoArenaManager = new ValoArenaManager(main, this);
    }

}