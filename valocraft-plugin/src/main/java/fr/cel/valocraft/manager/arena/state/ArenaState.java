package fr.cel.valocraft.manager.arena.state;

import javax.annotation.Nullable;

import org.bukkit.event.Listener;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.listener.state.StateListenerProvider;
import fr.cel.valocraft.manager.arena.Arena;

public abstract class ArenaState implements Listener {

    private Arena arena;
    private StateListenerProvider listenerProvider;

    public ArenaState(Arena arena) {
        this.arena = arena;
    }

    public void onEnable(ValoCraft main) {
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

    public Arena getArena() {
        return arena;
    }
    
}