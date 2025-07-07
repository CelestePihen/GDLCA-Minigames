package fr.cel.cachecache.map.state;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import lombok.Getter;
import org.bukkit.event.Listener;

public abstract class MapState implements Listener {

    @Getter private final CCMap map;
    @Getter private final String name;
    private StateListenerProvider listenerProvider;

    public MapState(String name, CCMap map) {
        this.name = name;
        this.map = map;
    }

    public void onEnable(CacheCache main) {
        main = map.getGameManager().getMain();

        listenerProvider = getListenerProvider();
        if (listenerProvider != null) listenerProvider.onEnable(main);
    }
   
    public void onDisable() {
        if (listenerProvider != null) listenerProvider.onDisable();
    }

    /**
     * Return a new instance of StateListenerProvider (or null)
     */
    public abstract StateListenerProvider getListenerProvider();
    
}