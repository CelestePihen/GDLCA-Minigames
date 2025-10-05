package fr.cel.valocraft.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.valocraft.ValoCraft;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GameManager {

    @Getter private final ValoCraft main;
    @Getter private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();
    @Getter private final Component prefix = Component.text("[Valocraft]", NamedTextColor.GOLD).append(Component.text(" ", NamedTextColor.WHITE));

    public GameManager(ValoCraft main) {
        this.main = main;
    }

    public void reloadArenaManager() {
        main.setValoArenaManager(new ValoArenaManager(main));
    }

}