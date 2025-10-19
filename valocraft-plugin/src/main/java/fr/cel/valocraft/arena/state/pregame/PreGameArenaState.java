package fr.cel.valocraft.arena.state.pregame;

import fr.cel.valocraft.Valocraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.ArenaState;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.arena.state.provider.pregame.PreGameListenerProvider;

public class PreGameArenaState extends ArenaState {

    public PreGameArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(Valocraft main) {
        super.onEnable(main);
        arena.hideInvisibleBarriers();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new PreGameListenerProvider(arena);
    }

}