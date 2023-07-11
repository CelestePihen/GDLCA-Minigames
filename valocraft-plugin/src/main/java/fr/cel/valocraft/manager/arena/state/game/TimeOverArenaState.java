package fr.cel.valocraft.manager.arena.state.game;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.listener.state.StateListenerProvider;
import fr.cel.valocraft.listener.state.game.TimeOverListenerProvider;
import fr.cel.valocraft.manager.arena.Arena;
import fr.cel.valocraft.manager.arena.state.ArenaState;
import fr.cel.valocraft.manager.arena.timer.game.TimeOverArenaTask;
import lombok.Getter;

public class TimeOverArenaState extends ArenaState {

    @Getter private TimeOverArenaTask timeOverArenaTask;
    private int timer = 10;

    public TimeOverArenaState(Arena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        timeOverArenaTask = new TimeOverArenaTask(getArena(), timer);
        timeOverArenaTask.runTaskTimer(main, 0, 20);

    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (timeOverArenaTask != null) timeOverArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new TimeOverListenerProvider(getArena());
    }

}