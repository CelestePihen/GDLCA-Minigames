package fr.cel.parkour.manager;

import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.parkour.Parkour;
import fr.cel.parkour.manager.area.MapManager;
import lombok.Getter;

public class ParkourGameManager {
    
    @Getter private final Parkour main;
    @Getter private MapManager mapManager;
    @Getter private final String prefix = ChatUtility.format("&6[Parkour] &f");

    @Getter private static ParkourGameManager gameManager;

    public ParkourGameManager(Parkour main) {
        gameManager = this;
        this.main = main;
        this.mapManager = new MapManager(main);
    }

    public void reloadArenaManager() {
        this.mapManager = new MapManager(main);
    }

}