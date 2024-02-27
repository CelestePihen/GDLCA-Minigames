package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandGeneralExecutor implements CommandExecutor {

    private final Map<String, AbstractCommand> commands = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.commands.containsKey(cmd.getName())) {
            return true;
        } else {
            AbstractCommand abstractCommand = this.commands.get(cmd.getName());
            if (abstractCommand.isNeedPlayer() && !(sender instanceof Player)) {
                sender.sendMessage(GameAPI.getInstance().getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
                return true;
            }

            else if (!sender.hasPermission(abstractCommand.getPermission()) && abstractCommand.isPermissionRequired()) {
                sender.sendMessage(GameAPI.getInstance().getPrefix() + "Vous n'avez pas la permission d'effectuer cette commande.");
                return true;
            }

            else {
                abstractCommand.onExecute(sender, args);
            }
        }
        return false;
    }

    public void addCommand(String cmd, AbstractCommand abstractCommand) {
        this.commands.put(cmd, abstractCommand);
    }

    public void removeCommand(String cmd) {
        this.commands.remove(cmd);
    }

}