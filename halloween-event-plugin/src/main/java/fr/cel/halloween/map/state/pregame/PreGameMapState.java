package fr.cel.halloween.map.state.pregame;

import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.providers.StateListenerProvider;
import fr.cel.halloween.map.providers.pregame.PreGameListenerProvider;
import fr.cel.halloween.map.state.MapState;

import javax.annotation.Nullable;

public class PreGameMapState extends MapState {

    public PreGameMapState(HalloweenMap arena) {
        super("Pré-lancement", arena);
    }

    @Override
    public void onEnable(HalloweenEvent main) {
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