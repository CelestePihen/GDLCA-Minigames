package fr.cel.valocraft.arena.state;

import fr.cel.valocraft.Valocraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;

public abstract class ArenaState implements Listener {

    protected final ValoArena arena;
    private StateListenerProvider listenerProvider;

    public ArenaState(ValoArena arena) {
        this.arena = arena;
    }

    public void onEnable(Valocraft main) {
        listenerProvider = getListenerProvider();
        if (listenerProvider != null) listenerProvider.onEnable(main);
    }
   
    public void onDisable() {
        if (listenerProvider != null) listenerProvider.onDisable();
    }

    /**
     * Return a new instance of StateListenerProvider (or null)
     */
    @Nullable
    public abstract StateListenerProvider getListenerProvider();

}