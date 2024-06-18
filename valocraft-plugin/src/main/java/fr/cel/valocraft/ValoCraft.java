package fr.cel.valocraft;

import fr.cel.gameapi.GameAPI;
import fr.cel.valocraft.commands.ValoCommands;
import fr.cel.valocraft.arena.ValoArena;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.valocraft.manager.GameManager;

public class ValoCraft extends JavaPlugin {

    @Getter
    private static GameManager gameManager;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this);

        GameAPI.getInstance().getCommandsManager().addCommand("valocraft", new ValoCommands(gameManager), this);
    }

    @Override
    public void onDisable() {
        for (ValoArena arena : gameManager.getValoArenaManager().getArenas()) {
            arena.removePlayersToBossBar();
        }
    }

}