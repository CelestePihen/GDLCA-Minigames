package fr.cel.hub.listener;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.Replacement;
import org.bukkit.event.Listener;

public class HListener extends Replacement implements Listener {

    protected final Hub main;

    public HListener(Hub main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

}
