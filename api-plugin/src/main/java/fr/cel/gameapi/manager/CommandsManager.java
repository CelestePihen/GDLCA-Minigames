package fr.cel.gameapi.manager;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.command.CommandGeneralExecutor;
import org.bukkit.command.PluginCommand;

public class CommandsManager extends ManagerAPI {

    private CommandGeneralExecutor commandGeneralExecutor;

    public void enable() {
        commandGeneralExecutor = new CommandGeneralExecutor();
    }

    public void addCommand(PluginCommand pluginCommand, AbstractCommand abstractCommand) {
        if (pluginCommand == null) return;

        pluginCommand.setExecutor(commandGeneralExecutor);
        this.commandGeneralExecutor.addCommand(pluginCommand.getName(), abstractCommand);
    }

}