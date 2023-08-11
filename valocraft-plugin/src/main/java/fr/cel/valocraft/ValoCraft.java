package fr.cel.valocraft;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.valocraft.commands.ValoCommands;
import fr.cel.valocraft.commands.ValoCompleter;
import fr.cel.valocraft.manager.ValoGameManager;

public class ValoCraft extends JavaPlugin {

    @Getter private static ValoGameManager gameManager;

    @Override
    public void onEnable() {
        gameManager = new ValoGameManager(this);
        gameManager.loadInventories();

        this.getCommand("valocraft").setExecutor(new ValoCommands(gameManager));
        this.getCommand("valocraft").setTabCompleter(new ValoCompleter());
    }

    @Override
    public void onDisable() {}

}