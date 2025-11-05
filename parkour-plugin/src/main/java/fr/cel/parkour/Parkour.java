package fr.cel.parkour;

import fr.cel.gameapi.manager.command.CommandsManager;
import fr.cel.parkour.commands.ParkourCommands;
import fr.cel.parkour.manager.GameManager;
import fr.cel.parkour.manager.ParkourMapManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Parkour extends JavaPlugin {

    private GameManager gameManager;
    @Setter private ParkourMapManager parkourMapManager;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager(this);
        this.parkourMapManager = new ParkourMapManager(this);

       new CommandsManager(this).addCommand("parkour", new ParkourCommands(gameManager));
    }

}