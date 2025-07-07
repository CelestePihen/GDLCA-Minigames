package fr.cel.cachecache.map.state.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import fr.cel.cachecache.map.listeners.game.PlayingListenerProvider;
import fr.cel.cachecache.map.state.MapState;
import fr.cel.cachecache.map.timer.game.GroundItemsMapTask;
import fr.cel.cachecache.map.timer.game.PlayingMapTask;
import fr.cel.cachecache.map.timer.game.wolf.PlayingBecomeWolfMapTask;
import fr.cel.cachecache.map.timer.game.wolf.PlayingWolfMapTask;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayingMapState extends MapState {

    private PlayingMapTask playingMapTask;
    private GroundItemsMapTask groundItemsMapTask;

    private PlayingWolfMapTask playingWolfMapTask;
    @Setter private PlayingBecomeWolfMapTask playingBecomeWolfMapTask;


    public PlayingMapState(CCMap map) {
        super("En partie", map);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

        getMap().setNbPlayerBeginning(getMap().getPlayers().size());
        getMap().getCheckAdvancements().startAllChecks();

        switch (getMap().getCcMode()) {
            case Classique, TousContreUn -> {
                playingMapTask = new PlayingMapTask(getMap());
                playingMapTask.runTaskTimer(main, 0, 20);
            }

            case LoupToucheTouche -> {
                getMap().setTimer(600); // 10 minutes pour le timer
                playingWolfMapTask = new PlayingWolfMapTask(getMap(), this);
                playingWolfMapTask.runTaskTimer(main, 0, 20);
            }

            case PluieDeBonus -> {
                playingMapTask = new PlayingMapTask(getMap());
                playingMapTask.runTaskTimer(main, 0, 20);

                groundItemsMapTask = new GroundItemsMapTask(getMap());
                groundItemsMapTask.runTaskTimer(main, 0, 1200);
            }

            case Normal, TwoHuntersAtStart, Beta -> {
                playingMapTask = new PlayingMapTask(getMap());
                playingMapTask.runTaskTimer(main, 0, 20);

                groundItemsMapTask = new GroundItemsMapTask(getMap());
                groundItemsMapTask.runTaskTimer(main, 0, 2400);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (playingMapTask != null) playingMapTask.cancel();
        if (playingWolfMapTask != null) playingWolfMapTask.cancel();
        if (playingBecomeWolfMapTask != null) playingBecomeWolfMapTask.cancel();
        if (groundItemsMapTask != null) groundItemsMapTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new PlayingListenerProvider(getMap());
    }

}
