package fr.cel.pvp;

import fr.cel.gameapi.GameAPI;
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

        GameAPI.getInstance().getCommandsManager().addCommand("pvp", new PVPCommands(gameManager), this);
    }

    @Override
    public void onDisable() {}

}