package fr.cel.cachecache.map.state.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import fr.cel.cachecache.map.state.MapState;

import javax.annotation.Nullable;

public class InitMapState extends MapState {

    public InitMapState(CCMap map) {
        super("Initialisation", map);
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