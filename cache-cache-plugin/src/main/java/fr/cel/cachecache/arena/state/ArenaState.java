package fr.cel.cachecache.arena.state;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.providers.StateListenerProvider;
import lombok.Getter;
import org.bukkit.event.Listener;

public abstract class ArenaState implements Listener {

    @Getter private final CCArena arena;
    @Getter private final String name;
    private StateListenerProvider listenerProvider;

    public ArenaState(String name, CCArena arena) {
        this.name = name;
        this.arena = arena;
    }

    public void onEnable(CacheCache main) {
        main = arena.getGameManager().getMain();

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