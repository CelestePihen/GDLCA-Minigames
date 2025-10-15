package fr.cel.valocraft;

import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.commands.ValoCommands;
import fr.cel.valocraft.manager.GameManager;
import fr.cel.valocraft.manager.ValoArenaManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Valocraft extends JavaPlugin {

    private GameManager gameManager;
    @Setter private ValoArenaManager valoArenaManager;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager(this);
        gameManager.reloadArenaManager();

        new CommandsManager(this).addCommand("valocraft", new ValoCommands(gameManager));
    }

    @Override
    public void onDisable() {
        for (ValoArena arena : valoArenaManager.getArenas().values()) {
            arena.removePlayersToBossBar();
        }
    }

}