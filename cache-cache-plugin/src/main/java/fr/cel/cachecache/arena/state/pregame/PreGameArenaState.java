package fr.cel.cachecache.arena.state.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.listeners.StateListenerProvider;
import fr.cel.cachecache.arena.listeners.pregame.PreGameListenerProvider;
import fr.cel.cachecache.arena.state.ArenaState;

import javax.annotation.Nullable;

public class PreGameArenaState extends ArenaState {

    public PreGameArenaState(CCArena arena) {
        super("Pré-lancement", arena);
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
        return new PreGameListenerProvider(getArena());
    }

}