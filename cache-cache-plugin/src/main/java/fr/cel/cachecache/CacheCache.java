package fr.cel.cachecache;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.cachecache.commands.CCCommands;
import fr.cel.cachecache.commands.CCCompleter;
import fr.cel.cachecache.listener.BlockListener;
import fr.cel.cachecache.manager.GameManager;

public class CacheCache extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        super.onEnable();

        this.gameManager = new GameManager(this);

        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(new BlockListener(gameManager), this);

        this.getCommand("cachecache").setExecutor(new CCCommands(gameManager));
        this.getCommand("cachecache").setTabCompleter(new CCCompleter());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}