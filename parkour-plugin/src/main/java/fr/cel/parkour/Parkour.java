package fr.cel.parkour;

import fr.cel.parkour.commands.ParkourCommands;
import fr.cel.parkour.commands.ParkourCompleter;
import fr.cel.parkour.manager.ParkourGameManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Parkour extends JavaPlugin {

    private ParkourGameManager gameManager;

    @Override
    public void onEnable() {
        super.onEnable();
        
        this.gameManager = new ParkourGameManager(this);

        this.getCommand("parkour").setExecutor(new ParkourCommands(gameManager));
        this.getCommand("parkour").setTabCompleter(new ParkourCompleter());
    }

    @Override
    public void onDisable() {}

}