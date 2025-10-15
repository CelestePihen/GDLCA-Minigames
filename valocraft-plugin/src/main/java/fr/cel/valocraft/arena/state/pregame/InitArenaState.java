package fr.cel.valocraft.arena.state.pregame;

import fr.cel.valocraft.Valocraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.ArenaState;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import org.jetbrains.annotations.Nullable;

public class InitArenaState extends ArenaState {

    public InitArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(Valocraft main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public @Nullable StateListenerProvider getListenerProvider() {
        return null;
    }
    
}