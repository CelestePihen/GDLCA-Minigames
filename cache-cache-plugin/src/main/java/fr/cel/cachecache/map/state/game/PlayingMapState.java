package fr.cel.cachecache.map.state.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import fr.cel.cachecache.map.listeners.game.PlayingListenerProvider;
import fr.cel.cachecache.map.state.MapState;
import fr.cel.cachecache.map.timer.game.GiftsMapTask;
import fr.cel.cachecache.map.timer.game.GroundItemsMapTask;
import fr.cel.cachecache.map.timer.game.PlayingMapTask;
import fr.cel.cachecache.map.timer.game.wolf.PlayingBecomeWolfMapTask;
import fr.cel.cachecache.map.timer.game.wolf.PlayingWolfMapTask;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public class PlayingMapState extends MapState {

    // Winter Event 2025 Start
    private static final int MIN_SECONDS_GIFTS_TASK = 180; // 3 minutes
    private static final int MAX_SECONDS_GIFTS_TASK = 300; // 5 minutes
    // Winter Event 2025 End

    private CacheCache main;

    private PlayingMapTask playingMapTask;
    private GroundItemsMapTask groundItemsMapTask;

    private PlayingWolfMapTask playingWolfMapTask;
    @Setter private PlayingBecomeWolfMapTask playingBecomeWolfMapTask;

    // Winter Event 2025 Start
    private GiftsMapTask giftsMapTask;
    // Winter Event 2025 End

    public PlayingMapState(CCMap map) {
        super("En partie", map);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

        this.main = main;

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
                groundItemsMapTask.runTaskTimer(main, 0, 20*60); // Toutes les minutes
            }

            case Normal, TwoHuntersAtStart, Beta -> {
                playingMapTask = new PlayingMapTask(getMap());
                playingMapTask.runTaskTimer(main, 0, 20);

                groundItemsMapTask = new GroundItemsMapTask(getMap());
                groundItemsMapTask.runTaskTimer(main, 0, 20*120); // Toutes les 2 minutes

                giftsMapTask = new GiftsMapTask(getMap());
                giftsMapTask.runTaskLater(main, ThreadLocalRandom.current().nextInt(MIN_SECONDS_GIFTS_TASK, MAX_SECONDS_GIFTS_TASK + 1) * 20L); // Début entre 3 et 5 minutes
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

        // Winter Event 2025 Start
        if (giftsMapTask != null) giftsMapTask.cancel();
        // Winter Event 2025 End
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new PlayingListenerProvider(getMap());
    }

    // Winter Event 2025 Start
    public void runNewGiftTask() {
        giftsMapTask = new GiftsMapTask(getMap());
        giftsMapTask.runTaskLater(main, ThreadLocalRandom.current().nextInt(MIN_SECONDS_GIFTS_TASK, MAX_SECONDS_GIFTS_TASK + 1) * 20L);
    }
    // Winter Event 2025 End

}
