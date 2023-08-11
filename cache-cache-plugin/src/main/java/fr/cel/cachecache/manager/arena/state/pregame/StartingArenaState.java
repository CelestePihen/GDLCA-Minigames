package fr.cel.cachecache.manager.arena.state.pregame;

import org.bukkit.GameMode;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.arena.state.ArenaState;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;
import fr.cel.cachecache.manager.arena.state.providers.pregame.StartingListenerProvider;
import fr.cel.cachecache.manager.arena.timer.pregame.StartingArenaTask;
import lombok.Getter;

public class StartingArenaState extends ArenaState {

    @Getter private StartingArenaTask startingArenaTask;

    public StartingArenaState(CCArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

        getArena().clearPlayers();
        getArena().setGameModePlayers(GameMode.ADVENTURE);
        getArena().setSpawnPoint();
        getArena().clearPotionEffects();
        getArena().setLevel(0);

        startingArenaTask = new StartingArenaTask(getArena(), 11);
        startingArenaTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (startingArenaTask != null) startingArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new StartingListenerProvider(getArena());
    }

}