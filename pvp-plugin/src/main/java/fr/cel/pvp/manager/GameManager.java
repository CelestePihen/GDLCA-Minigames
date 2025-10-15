package fr.cel.pvp.manager;

import fr.cel.pvp.PVP;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
public class GameManager {

    @Getter private static final Component prefix = Component.empty().append(Component.text("[PVP]", NamedTextColor.GOLD)).append(Component.text(" "));

    private final PVP main;

    public GameManager(PVP main) {
        this.main = main;
    }

    /**
     * Permet de recharger les maps du PVP
     */
    public void reloadArenaManager() {
        main.setPvpArenaManager(new PVPArenaManager(main));
    }

}