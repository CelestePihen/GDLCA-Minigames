package fr.cel.cachecache;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.cachecache.commands.CCCommands;
import fr.cel.cachecache.commands.CCCompleter;
import fr.cel.cachecache.manager.CCGameManager;

public class CacheCache extends JavaPlugin {

    @Getter private CCGameManager gameManager;

    @Override
    public void onEnable() {
        this.gameManager = new CCGameManager(this);

        this.getCommand("cachecache").setExecutor(new CCCommands(gameManager));
        this.getCommand("cachecache").setTabCompleter(new CCCompleter());
    }

    @Override
    public void onDisable() {}

}