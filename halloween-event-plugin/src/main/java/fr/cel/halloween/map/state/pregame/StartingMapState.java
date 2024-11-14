package fr.cel.halloween.map.state.pregame;

import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.providers.StateListenerProvider;
import fr.cel.halloween.map.providers.pregame.StartingListenerProvider;
import fr.cel.halloween.map.state.MapState;
import fr.cel.halloween.map.timer.pregame.StartingMapTask;
import lombok.Getter;
import org.bukkit.GameMode;

@Getter
public class StartingMapState extends MapState {

    private StartingMapTask startingArenaTask;

    public StartingMapState(HalloweenMap map) {
        super("Lancement", map);
    }

    @Override
    public void onEnable(HalloweenEvent main) {
        super.onEnable(main);

        getMap().clearPlayers();
        getMap().setGameModePlayers(GameMode.ADVENTURE);
        getMap().setSpawnPoint();
        getMap().clearPotionEffects();
        getMap().setLevel(0);

        startingArenaTask = new StartingMapTask(getMap(), 11);
        startingArenaTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (startingArenaTask != null) startingArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new StartingListenerProvider(getMap());
    }

}