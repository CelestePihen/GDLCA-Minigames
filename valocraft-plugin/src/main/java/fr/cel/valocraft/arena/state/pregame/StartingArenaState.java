package fr.cel.valocraft.arena.state.pregame;

import java.util.UUID;

import fr.cel.valocraft.manager.Role;
import fr.cel.valocraft.manager.ValoTeam;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.arena.state.provider.pregame.StartingListenerProvider;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.ArenaState;
import fr.cel.valocraft.arena.timer.pregame.StartingArenaTask;

@Getter
public class StartingArenaState extends ArenaState {

    private StartingArenaTask arenaStartingTask;

    public StartingArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        setSpawnPoint();

        int timer = 10;
        arenaStartingTask = new StartingArenaTask(arena, timer);
        arenaStartingTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (arenaStartingTask != null) arenaStartingTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new StartingListenerProvider(arena);
    }

    private void setSpawnPoint() {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.setRespawnLocation(arena.getSpawnLoc(), true);
        }
    }

}