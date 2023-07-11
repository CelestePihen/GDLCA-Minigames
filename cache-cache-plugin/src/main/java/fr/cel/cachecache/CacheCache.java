package fr.cel.cachecache;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.cachecache.commands.CCCommands;
import fr.cel.cachecache.commands.CCCompleter;
import fr.cel.cachecache.listener.BlockListener;
import fr.cel.cachecache.manager.GameManager;

public class CacheCache extends JavaPlugin {

    private GameManager gameManager;

    // quand le plugin s'allume
    @Override
    public void onEnable() {
        super.onEnable();
        
        // Permet de créer une instance de GameManager
        this.gameManager = new GameManager(this);

        PluginManager pm = this.getServer().getPluginManager();

        // Fait en sorte que les events dans cette classe soient détectés
        pm.registerEvents(new BlockListener(gameManager), this);

        // Permet d'enregistrer la commande "cachecache"
        this.getCommand("cachecache").setExecutor(new CCCommands(gameManager));
        // Permet d'ajouter les suggestions quand on écrit la commande
        this.getCommand("cachecache").setTabCompleter(new CCCompleter());
    }

    // quand le plugin s'éteint
    @Override
    public void onDisable() {
        super.onDisable();
    }

}