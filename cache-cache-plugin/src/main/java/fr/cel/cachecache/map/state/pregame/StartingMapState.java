package fr.cel.cachecache.map.state.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import fr.cel.cachecache.map.listeners.pregame.StartingListenerProvider;
import fr.cel.cachecache.map.state.MapState;
import fr.cel.cachecache.map.timer.pregame.StartingMapTask;
import lombok.Getter;
import org.bukkit.GameMode;

@Getter
public class StartingMapState extends MapState {

    private StartingMapTask startingMapTask;

    public StartingMapState(CCMap map) {
        super("Lancement", map);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

        getMap().clearPlayers();
        getMap().setGameModePlayers(GameMode.ADVENTURE);
        getMap().setSpawnPoint();
        getMap().clearPotionEffects();
        getMap().giveHungerEffect();
        getMap().setLevel(0);

        startingMapTask = new StartingMapTask(getMap(), 11);
        startingMapTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (startingMapTask != null) startingMapTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new StartingListenerProvider(getMap());
    }

}