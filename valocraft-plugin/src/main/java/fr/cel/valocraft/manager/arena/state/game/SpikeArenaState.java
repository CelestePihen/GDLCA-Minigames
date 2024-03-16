package fr.cel.valocraft.manager.arena.state.game;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.state.provider.game.SpikeListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.valocraft.manager.arena.state.ArenaState;
import fr.cel.valocraft.manager.arena.timer.game.SpikeArenaTask;
import lombok.Getter;

@Getter
public class SpikeArenaState extends ArenaState {

    private SpikeArenaTask spikeArenaTask;

    public SpikeArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        arena.sendMessage("Spike posé !");
        arena.sendTitle("Spike posé !", "");

        spikeArenaTask = new SpikeArenaTask(arena, 45);
        spikeArenaTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (spikeArenaTask != null) spikeArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new SpikeListenerProvider(arena);
    }
    
}
