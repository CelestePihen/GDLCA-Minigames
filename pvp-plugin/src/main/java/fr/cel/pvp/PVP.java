package fr.cel.pvp;

import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.pvp.commands.PVPCommands;
import fr.cel.pvp.commands.PVPCompleter;
import fr.cel.pvp.listener.BlockListener;
import fr.cel.pvp.manager.GameManager;

public class PVP extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        super.onEnable();
        
        this.gameManager = new GameManager(this);

        getServer().getPluginManager().registerEvents(new BlockListener(gameManager), this);

        this.getCommand("pvp").setExecutor(new PVPCommands(gameManager));
        this.getCommand("pvp").setTabCompleter(new PVPCompleter());
    }

    @Override
    public void onDisable() {}

}