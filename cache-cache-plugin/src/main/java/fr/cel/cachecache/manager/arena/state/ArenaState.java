package fr.cel.cachecache.manager.arena.state;

import javax.annotation.Nullable;

import org.bukkit.event.Listener;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.Arena;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;
import lombok.Getter;

public abstract class ArenaState implements Listener {

    @Getter private Arena arena;
    private StateListenerProvider listenerProvider;

    public ArenaState(Arena arena) {
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
     * @return
     */
    @Nullable
    public abstract StateListenerProvider getListenerProvider();
    
}