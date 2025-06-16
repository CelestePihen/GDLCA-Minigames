package fr.cel.valocraft;

import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.commands.ValoCommands;
import fr.cel.valocraft.manager.GameManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ValoCraft extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this);

        new CommandsManager(this).addCommand("valocraft", new ValoCommands(gameManager));
    }

    @Override
    public void onDisable() {
        for (ValoArena arena : gameManager.getValoArenaManager().getArenas()) {
            arena.removePlayersToBossBar();
        }
    }

}