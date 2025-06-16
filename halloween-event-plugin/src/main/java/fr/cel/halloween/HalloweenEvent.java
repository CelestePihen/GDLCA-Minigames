package fr.cel.halloween;

import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.halloween.commands.HalloweenCommands;
import fr.cel.halloween.manager.GameManager;
import fr.cel.halloween.manager.HalloweenMapManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class HalloweenEvent extends JavaPlugin {

    private GameManager gameManager;
    @Setter private HalloweenMapManager halloweenMapManager;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager(this);
        this.halloweenMapManager = new HalloweenMapManager(this);

        new CommandsManager(this).addCommand("halloween", new HalloweenCommands(gameManager));
    }

    @Override
    public void onDisable() { }

}