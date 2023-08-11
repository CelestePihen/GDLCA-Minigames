package fr.cel.cachecache.manager.arena.state.pregame;

import javax.annotation.Nullable;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.arena.state.ArenaState;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;

public class InitArenaState extends ArenaState {

    public InitArenaState(CCArena arena) {
        super(arena);
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