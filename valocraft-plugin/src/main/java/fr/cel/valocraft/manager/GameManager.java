package fr.cel.valocraft.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.valocraft.Valocraft;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class GameManager {

    @Getter private final Valocraft main;
    @Getter private final Component prefix = Component.empty().append(Component.text("[Valocraft]", NamedTextColor.GOLD)).append(Component.text(" "));

    @Getter private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();

    public GameManager(Valocraft main) {
        this.main = main;
    }

    public void reloadArenaManager() {
        main.setValoArenaManager(new ValoArenaManager(main));
    }

}