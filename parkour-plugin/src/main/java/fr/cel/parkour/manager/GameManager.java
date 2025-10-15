package fr.cel.parkour.manager;

import fr.cel.parkour.Parkour;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
public class GameManager {

    @Getter private static final Component prefix = Component.empty().append(Component.text("[Parkour]", NamedTextColor.GOLD)).append(Component.text(" "));
    private final Parkour main;

    public GameManager(Parkour main) {
        this.main = main;
    }

    public void reloadMapManager() {
        main.setParkourMapManager(new ParkourMapManager(main));
    }

}