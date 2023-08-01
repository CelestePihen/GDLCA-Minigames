package fr.cel.pvp;

import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.pvp.commands.PVPCommands;
import fr.cel.pvp.commands.PVPCompleter;
import fr.cel.pvp.manager.PVPGameManager;

public class PVP extends JavaPlugin {

    private PVPGameManager gameManager;

    @Override
    public void onEnable() {
        super.onEnable();
        
        this.gameManager = new PVPGameManager(this);

        this.getCommand("pvp").setExecutor(new PVPCommands(gameManager));
        this.getCommand("pvp").setTabCompleter(new PVPCompleter());
    }

    @Override
    public void onDisable() {}

}