package fr.cel.cachecache.arena.state.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.listeners.StateListenerProvider;
import fr.cel.cachecache.arena.state.ArenaState;

import javax.annotation.Nullable;

public class InitArenaState extends ArenaState {

    public InitArenaState(CCArena arena) {
        super("Initialisation", arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Nullable
    @Override
    public StateListenerProvider getListenerProvider() {
        return null;
    }
    
}