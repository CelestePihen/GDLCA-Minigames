package fr.cel.cachecache.manager.arena.state.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.Arena;
import fr.cel.cachecache.manager.arena.state.ArenaState;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;
import fr.cel.cachecache.manager.arena.state.providers.game.PlayingListenerProvider;
import fr.cel.cachecache.manager.arena.timer.game.GroundItemsArenaTask;
import fr.cel.cachecache.manager.arena.timer.game.PlayingArenaTask;
import lombok.Getter;

public class PlayingArenaState extends ArenaState {

    @Getter private PlayingArenaTask playingArenaTask;
    @Getter private GroundItemsArenaTask groundItemsArenaTask;

    public PlayingArenaState(Arena arena) {
        super(arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

        playingArenaTask = new PlayingArenaTask(getArena());
        playingArenaTask.runTaskTimer(main, 0, 20);

        groundItemsArenaTask = new GroundItemsArenaTask(getArena());
        groundItemsArenaTask.runTaskTimer(main, 0, 2400);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (playingArenaTask != null) playingArenaTask.cancel();
        if (groundItemsArenaTask != null) groundItemsArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new PlayingListenerProvider(getArena());
    }

}
