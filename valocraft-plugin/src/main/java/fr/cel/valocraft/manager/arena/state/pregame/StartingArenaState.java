package fr.cel.valocraft.manager.arena.state.pregame;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.state.provider.pregame.StartingListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.valocraft.manager.arena.state.ArenaState;
import fr.cel.valocraft.manager.arena.timer.pregame.StartingArenaTask;

public class StartingArenaState extends ArenaState {

    private StartingArenaTask arenaStartingTask;

    public StartingArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        getArena().setGameModePlayers(GameMode.SURVIVAL);
        setSpawnPoint();

        int timer = 10;
        arenaStartingTask = new StartingArenaTask(getArena(), timer);
        arenaStartingTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (arenaStartingTask != null) arenaStartingTask.cancel();
    }

    public StartingArenaTask getArenaStartingTask() {
        return arenaStartingTask;
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new StartingListenerProvider(getArena());
    }

    private void setSpawnPoint() {
        for (UUID uuid : getArena().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            player.setBedSpawnLocation(getArena().getSpawnLoc(), true);
        }
    }

}