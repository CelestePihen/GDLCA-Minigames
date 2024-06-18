package fr.cel.pvp.manager;

import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.pvp.PVP;
import fr.cel.pvp.arena.PVPArenaManager;
import lombok.Getter;

@Getter
public class GameManager {
    
    private final PVP main;
    private final String prefix;

    public GameManager(PVP main) {
        this.main = main;
        this.prefix = ChatUtility.format("&6[PVP] &r");
    }

    /**
     * Permet de recharger les maps du PVP
     */
    public void reloadArenaManager() {
        main.setPvpArenaManager(new PVPArenaManager(main));
    }

}