package fr.cel.valocraft.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.valocraft.ValoCraft;
import lombok.Getter;

public class GameManager {

    @Getter private final ValoCraft main;
    @Getter private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();
    @Getter private final String prefix = ChatUtility.format("&6[Valocraft]&r ");

    public GameManager(ValoCraft main) {
        this.main = main;
    }

    public void reloadArenaManager() {
        main.setValoArenaManager(new ValoArenaManager(main));
    }

}