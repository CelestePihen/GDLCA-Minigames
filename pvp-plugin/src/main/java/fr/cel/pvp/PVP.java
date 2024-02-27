package fr.cel.pvp;

import fr.cel.gameapi.GameAPI;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.pvp.commands.PVPCommands;
import fr.cel.pvp.manager.PVPGameManager;

public class PVP extends JavaPlugin {

    private PVPGameManager gameManager;

    @Override
    public void onEnable() {
        super.onEnable();
        this.gameManager = new PVPGameManager(this);

        GameAPI.getInstance().getCommandsManager().addCommand(getCommand("pvp"), new PVPCommands(gameManager));
    }

    @Override
    public void onDisable() {}

}