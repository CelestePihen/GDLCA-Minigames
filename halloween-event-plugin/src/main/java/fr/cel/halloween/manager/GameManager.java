package fr.cel.halloween.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.halloween.HalloweenEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class GameManager {

    @Getter private static final Component prefix = Component.empty().append(Component.text("[Halloween Event]", NamedTextColor.GOLD)).append(Component.text(" "));

    private final HalloweenEvent main;

    private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();

    public GameManager(HalloweenEvent main) {
        this.main = main;
    }

    public void reloadMapManager() {
        main.setHalloweenMapManager(new HalloweenMapManager(main));
    }

}