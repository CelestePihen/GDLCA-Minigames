package fr.cel.parkour;

import fr.cel.gameapi.GameAPI;
import fr.cel.parkour.commands.ParkourCommands;
import fr.cel.parkour.manager.ParkourGameManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Parkour extends JavaPlugin {

    private ParkourGameManager gameManager;

    @Override
    public void onEnable() {
        super.onEnable();
        
        this.gameManager = new ParkourGameManager(this);

        GameAPI.getInstance().getCommandsManager().addCommand(getCommand("parkour"), new ParkourCommands(gameManager));
    }

    @Override
    public void onDisable() {}

}