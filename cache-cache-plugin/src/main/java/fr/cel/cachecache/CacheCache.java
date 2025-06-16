package fr.cel.cachecache;

import fr.cel.cachecache.commands.CCCommand;
import fr.cel.cachecache.manager.CCArenaManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.gameapi.manager.CommandsManager;
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

        gameManager.reloadArenaManager();
        gameManager.reloadTemporaryHub();

        new CommandsManager(this).addCommand("cachecache", new CCCommand(gameManager));
    }

    @Override
    public void onDisable() { }

}