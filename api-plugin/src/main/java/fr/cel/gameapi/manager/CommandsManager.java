package fr.cel.gameapi.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.command.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandsManager {

    private final GameAPI main;

    public CommandsManager(GameAPI main) {
        this.main = main;
    }

    /**
     * Enregistre toutes les commandes
     */
    public void registerCommands() {
        addCommand("coins", new CoinsCommand(main.getPlayerManager()), main);
        addCommand("friends", new FriendsCommand(main.getFriendsManager()), main);
        addCommand("gamecompass", new GameCompassComand(main.getPlayerManager()), main);
        addCommand("profile", new ProfileCommand(), main);
        addCommand("welcome", new WelcomeCommand(main.getPlayerManager()), main);
        addCommand("statistics", new StatisticsCommand(), main);
    }

    /**
     * Permet de créer une nouvelle commande
     * @param commandName Le nom de la commande
     * @param abstractCommand La classe de la commande
     * @param plugin Le plugin correspondant
     */
    public void addCommand(String commandName, AbstractCommand abstractCommand, JavaPlugin plugin) {
        PluginCommand pluginCommand = plugin.getCommand(commandName);

        if (pluginCommand == null) {
            Bukkit.getConsoleSender().sendMessage(GameAPI.getPrefix() + "La commande " + commandName + " n'a pas été mise dans le plugin.yml du plugin " + plugin.getName());
            return;
        }

        pluginCommand.setExecutor(abstractCommand);
        pluginCommand.setTabCompleter(abstractCommand);
    }

}