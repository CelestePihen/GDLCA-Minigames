package fr.cel.cachecache.arena.state.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.providers.StateListenerProvider;
import fr.cel.cachecache.arena.providers.pregame.StartingListenerProvider;
import fr.cel.cachecache.arena.state.ArenaState;
import fr.cel.cachecache.arena.timer.pregame.StartingArenaTask;
import lombok.Getter;
import org.bukkit.GameMode;

@Getter
public class StartingArenaState extends ArenaState {

    private StartingArenaTask startingArenaTask;

    public StartingArenaState(CCArena arena) {
        super("Lancement", arena);
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