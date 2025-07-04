package fr.cel.cachecache.arena.state.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.listeners.StateListenerProvider;
import fr.cel.cachecache.arena.listeners.game.PlayingListenerProvider;
import fr.cel.cachecache.arena.state.ArenaState;
import fr.cel.cachecache.arena.timer.game.GroundItemsArenaTask;
import fr.cel.cachecache.arena.timer.game.PlayingArenaTask;
import fr.cel.cachecache.arena.timer.game.wolf.PlayingBecomeWolfArenaTask;
import fr.cel.cachecache.arena.timer.game.wolf.PlayingWolfArenaTask;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayingArenaState extends ArenaState {

    private PlayingArenaTask playingArenaTask;
    private GroundItemsArenaTask groundItemsArenaTask;

    private PlayingWolfArenaTask playingWolfArenaTask;
    @Setter private PlayingBecomeWolfArenaTask playingBecomeWolfArenaTask;


    public PlayingArenaState(CCArena arena) {
        super("En partie", arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

        getArena().setNbPlayerBeginning(getArena().getPlayers().size());
        getArena().getCheckAdvancements().startAllChecks();

        switch (getArena().getCcMode()) {
            case Classique, TousContreUn -> {
                playingArenaTask = new PlayingArenaTask(getArena());
                playingArenaTask.runTaskTimer(main, 0, 20);
            }

            case LoupToucheTouche -> {
                getArena().setTimer(600); // 10 minutes pour le timer
                playingWolfArenaTask = new PlayingWolfArenaTask(getArena(), this);
                playingWolfArenaTask.runTaskTimer(main, 0, 20);
            }

            case PluieDeBonus -> {
                playingArenaTask = new PlayingArenaTask(getArena());
                playingArenaTask.runTaskTimer(main, 0, 20);

                groundItemsArenaTask = new GroundItemsArenaTask(getArena());
                groundItemsArenaTask.runTaskTimer(main, 0, 1200);
            }

            case Normal, TwoHuntersAtStart, Beta -> {
                playingArenaTask = new PlayingArenaTask(getArena());
                playingArenaTask.runTaskTimer(main, 0, 20);

                groundItemsArenaTask = new GroundItemsArenaTask(getArena());
                groundItemsArenaTask.runTaskTimer(main, 0, 2400);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (playingArenaTask != null) playingArenaTask.cancel();
        if (playingWolfArenaTask != null) playingWolfArenaTask.cancel();
        if (playingBecomeWolfArenaTask != null) playingBecomeWolfArenaTask.cancel();
        if (groundItemsArenaTask != null) groundItemsArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new PlayingListenerProvider(getArena());
    }

}
