package fr.cel.pvp;

import fr.cel.gameapi.manager.CommandsManager;
import fr.cel.pvp.arena.PVPArenaManager;
import fr.cel.pvp.commands.PVPCommands;
import fr.cel.pvp.manager.GameManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PVP extends JavaPlugin {

    private GameManager gameManager;
    @Setter private PVPArenaManager pvpArenaManager;

    @Override
    public void onEnable() {
        super.onEnable();

        this.gameManager = new GameManager(this);
        this.pvpArenaManager = new PVPArenaManager(this);

        new CommandsManager(this).addCommand("pvp", new PVPCommands(gameManager));
    }

    @Override
    public void onDisable() {}

}