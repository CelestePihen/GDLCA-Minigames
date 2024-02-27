package fr.cel.valocraft.manager.arena.state.game;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.state.provider.game.PlayingListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.valocraft.manager.arena.state.ArenaState;
import fr.cel.valocraft.manager.arena.timer.game.PlayingAreraTask;

public class PlayingArenaState extends ArenaState {

    private PlayingAreraTask playingAreraTask;

    public PlayingArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        playingAreraTask = new PlayingAreraTask(getArena(), 100);
        playingAreraTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (playingAreraTask != null) playingAreraTask.cancel();
    }

    public PlayingAreraTask getPlayingArenaTask() {
        return playingAreraTask;
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new PlayingListenerProvider(getArena());
    }
    
}
