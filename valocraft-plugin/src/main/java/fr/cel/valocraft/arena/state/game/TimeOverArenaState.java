package fr.cel.valocraft.arena.state.game;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.ArenaState;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.arena.state.provider.game.TimeOverListenerProvider;
import fr.cel.valocraft.arena.timer.game.TimeOverArenaTask;
import lombok.Getter;

@Getter
public class TimeOverArenaState extends ArenaState {

    private TimeOverArenaTask timeOverArenaTask;

    public TimeOverArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        int timer = 10;
        timeOverArenaTask = new TimeOverArenaTask(arena, timer);
        timeOverArenaTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (timeOverArenaTask != null) timeOverArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new TimeOverListenerProvider(arena);
    }

}