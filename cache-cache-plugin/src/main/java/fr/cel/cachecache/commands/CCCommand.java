package fr.cel.cachecache.commands;

import fr.cel.cachecache.commands.subcommands.*;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.gameapi.command.AbstractCommandSub;

public class CCCommand extends AbstractCommandSub {

    public CCCommand(GameManager gameManager) {
        super("cachecache:admin", false, true);

        // Register Sub-Commands
        registerSubCommand(new CalculSubCommand(gameManager));
        registerSubCommand(new EnableTemporarySubCommand(gameManager));
        registerSubCommand(new InformationSubCommand(gameManager));
        registerSubCommand(new JoinSubCommand(gameManager));
        registerSubCommand(new ListPlayerSubCommand(gameManager));
        registerSubCommand(new ListSubCommand(gameManager));
        registerSubCommand(new ReloadSubCommand(gameManager));
        registerSubCommand(new ReloadTemporarySubCommand(gameManager));
        registerSubCommand(new StartSubCommand(gameManager));
        registerSubCommand(new TempHubSubCommand(gameManager));
    }

}