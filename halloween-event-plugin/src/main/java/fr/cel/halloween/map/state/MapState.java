package fr.cel.halloween.map.state;

import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.providers.StateListenerProvider;
import lombok.Getter;
import org.bukkit.event.Listener;

public abstract class MapState implements Listener {

    @Getter private final HalloweenMap map;
    @Getter private final String name;
    private StateListenerProvider listenerProvider;

    public MapState(String name, HalloweenMap map) {
        this.name = name;
        this.map = map;
    }

    public void onEnable(HalloweenEvent main) {
        main = map.getGameManager().getMain();

        listenerProvider = getListenerProvider();
        if (listenerProvider != null) listenerProvider.onEnable(main);
    }

    public void onDisable() {
        if (listenerProvider != null) listenerProvider.onDisable();
    }

    public abstract StateListenerProvider getListenerProvider();

}