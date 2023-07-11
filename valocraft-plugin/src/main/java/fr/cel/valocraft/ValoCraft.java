package fr.cel.valocraft;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.valocraft.commands.ValoCommands;
import fr.cel.valocraft.commands.ValoCompleter;
import fr.cel.valocraft.listener.BlockListener;
import fr.cel.valocraft.manager.GameManager;

public class ValoCraft extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        super.onEnable();
        
        this.gameManager = new GameManager(this);

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new BlockListener(gameManager), this);

        this.getCommand("valocraft").setExecutor(new ValoCommands(gameManager));
        this.getCommand("valocraft").setTabCompleter(new ValoCompleter());
    }

    @Override
    public void onDisable() {}

}