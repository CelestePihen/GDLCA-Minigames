package fr.cel.valocraft.manager.arena.state;

import javax.annotation.Nullable;

import lombok.Getter;
import org.bukkit.event.Listener;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;

public abstract class ArenaState implements Listener {

    protected final ValoArena arena;
    private StateListenerProvider listenerProvider;

    public ArenaState(ValoArena arena) {
        this.arena = arena;
    }

    public void onEnable(ValoCraft main) {
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