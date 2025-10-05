package fr.cel.valocraft.arena.state.game;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.ArenaState;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.arena.state.provider.game.SpikeListenerProvider;
import fr.cel.valocraft.arena.timer.game.SpikeArenaTask;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class SpikeArenaState extends ArenaState {

    private SpikeArenaTask spikeArenaTask;

    public SpikeArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        arena.sendMessage(Component.text("Spike posé !"));
        arena.sendTitle(Component.text("Spike posé !"), Component.empty());

        spikeArenaTask = new SpikeArenaTask(arena);
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
