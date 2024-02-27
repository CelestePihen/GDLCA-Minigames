package fr.cel.cachecache.manager.arena.state;

import javax.annotation.Nullable;

import fr.cel.cachecache.manager.arena.CCArena;
import org.bukkit.event.Listener;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;
import lombok.Getter;

public abstract class ArenaState implements Listener {

    @Getter private final CCArena arena;
    private StateListenerProvider listenerProvider;

    public ArenaState(CCArena arena) {
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