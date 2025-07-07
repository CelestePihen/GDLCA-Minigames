package fr.cel.gameapi.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandsManager {

    private final JavaPlugin main;

    public CommandsManager(JavaPlugin main) {
        this.main = main;
    }

    /**
     * Permet de créer une nouvelle commande
     * @param commandName Le nom de la commande
     * @param abstractCommand La classe de la commande
     */
    public void addCommand(String commandName, AbstractCommand abstractCommand) {
        PluginCommand pluginCommand = main.getCommand(commandName);

        if (pluginCommand == null) {
            Bukkit.getConsoleSender().sendMessage(GameAPI.getPrefix() + "La commande " + commandName + " n'a pas été mise dans le plugin.yml du plugin " + main.getName());
            return;
        }

        pluginCommand.setExecutor(abstractCommand);
        pluginCommand.setTabCompleter(abstractCommand);
    }

}