package fr.cel.cachecache;

import fr.cel.cachecache.commands.CCCommand;
import fr.cel.gameapi.GameAPI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.cachecache.manager.CCGameManager;

public class CacheCache extends JavaPlugin {

    @Getter private CCGameManager gameManager;

    @Override
    public void onEnable() {
        this.gameManager = new CCGameManager(this);

        GameAPI.getInstance().getCommandsManager().addCommand(getCommand("cachecache"), new CCCommand(gameManager));
    }

    @Override
    public void onDisable() {}

}