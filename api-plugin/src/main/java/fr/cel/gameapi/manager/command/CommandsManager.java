package fr.cel.gameapi.manager.command;

import fr.cel.gameapi.GameAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CommandsManager {

    private final JavaPlugin main;

    public CommandsManager(JavaPlugin main) {
        this.main = main;
    }

    /**
     * Registers a new command
     * @param commandName The name of the command
     * @param abstractCommand The command class to register
     */
    public void addCommand(String commandName, @NotNull AbstractCommand abstractCommand) {
        PluginCommand pluginCommand = main.getCommand(commandName);

        if (pluginCommand == null) {
            Bukkit.getConsoleSender().sendMessage(GameAPI.getPrefix()
                    .append(Component.text("La commande " + commandName + " n'a pas été mise dans le plugin.yml du plugin " + main.getName())));
            return;
        }

        pluginCommand.setExecutor(abstractCommand);
        pluginCommand.setTabCompleter(abstractCommand);
    }

}