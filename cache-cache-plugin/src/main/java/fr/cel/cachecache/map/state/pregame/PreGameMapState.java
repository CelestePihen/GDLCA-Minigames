package fr.cel.cachecache.map.state.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import fr.cel.cachecache.map.listeners.pregame.PreGameListenerProvider;
import fr.cel.cachecache.map.state.MapState;

import javax.annotation.Nullable;

public class PreGameMapState extends MapState {

    public PreGameMapState(CCMap map) {
        super("Pré-lancement", map);
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
        return new PreGameListenerProvider(getMap());
    }

}