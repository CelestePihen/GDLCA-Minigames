package fr.cel.parkour.manager;

import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.parkour.Parkour;
import lombok.Getter;

@Getter
public class GameManager {
    
    private final Parkour main;
    private final String prefix;

    public GameManager(Parkour main) {
        this.main = main;
        this.prefix = ChatUtility.format("&6[Parkour] &r");
    }

    public void reloadMapManager() {
        main.setParkourMapManager(new ParkourMapManager(main));
    }

}