package fr.cel.parkour;

import fr.cel.gameapi.GameAPI;
import fr.cel.parkour.commands.ParkourCommands;
import fr.cel.parkour.manager.GameManager;
import fr.cel.parkour.map.ParkourMapManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Parkour extends JavaPlugin {

    private GameManager gameManager;
    @Setter private ParkourMapManager parkourMapManager;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this);
        parkourMapManager = new ParkourMapManager(this);

        GameAPI.getInstance().getCommandsManager().addCommand("parkour", new ParkourCommands(gameManager), this);
    }

    @Override
    public void onDisable() {}

}