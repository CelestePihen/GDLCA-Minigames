package fr.cel.cachecache;

import fr.cel.cachecache.commands.CCCommand;
import fr.cel.cachecache.manager.CCArenaManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.gameapi.GameAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CacheCache extends JavaPlugin {

    private GameManager gameManager;
    @Setter private CCArenaManager ccArenaManager;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager(this);
        this.ccArenaManager = new CCArenaManager(this);

        gameManager.reloadTemporaryHub();

        GameAPI.getInstance().getCommandsManager().addCommand("cachecache", new CCCommand(gameManager),  this);
    }

    @Override
    public void onDisable() { }

}