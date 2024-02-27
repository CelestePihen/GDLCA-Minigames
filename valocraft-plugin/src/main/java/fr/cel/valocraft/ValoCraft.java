package fr.cel.valocraft;

import fr.cel.gameapi.GameAPI;
import fr.cel.valocraft.commands.ValoCommands;
import fr.cel.valocraft.manager.arena.ValoArena;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.valocraft.manager.ValoGameManager;

public class ValoCraft extends JavaPlugin {

    @Getter
    private static ValoGameManager gameManager;

    @Override
    public void onEnable() {
        gameManager = new ValoGameManager(this);

        GameAPI.getInstance().getCommandsManager().addCommand(getCommand("valocraft"), new ValoCommands(gameManager));
    }

    @Override
    public void onDisable() {
        for (ValoArena arena : gameManager.getArenaManager().getArenas()) {
            arena.removePlayersToBossBar();
        }
    }

}