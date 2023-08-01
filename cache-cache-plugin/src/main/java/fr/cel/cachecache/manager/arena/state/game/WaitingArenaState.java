package fr.cel.cachecache.manager.arena.state.game;

import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.CCArena;
import fr.cel.cachecache.manager.CCArena.HunterMode;
import fr.cel.cachecache.manager.arena.state.ArenaState;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;
import fr.cel.cachecache.manager.arena.state.providers.game.WaitingListenerProvider;
import fr.cel.cachecache.manager.arena.timer.game.WaitingArenaTask;
import lombok.Getter;

public class WaitingArenaState extends ArenaState {

    @Getter private WaitingArenaTask waitingArenaTask;

    public WaitingArenaState(CCArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

        Collections.shuffle(getArena().getPlayers());
        UUID randomUUID = getArena().getPlayers().get(0);
        Player player = Bukkit.getPlayer(randomUUID);

        getArena().becomeSeeker(player);
        player.teleport(getArena().getWaitingLoc());

        if (getArena().getHunterMode() == HunterMode.TwoHuntersAtStart) {
            UUID randomUUID2 = getArena().getPlayers().get(1);
            Player player2 = Bukkit.getPlayer(randomUUID2);
            getArena().becomeSeeker(player2);
            player2.teleport(getArena().getWaitingLoc());
        }

        for (UUID pls : getArena().getPlayers()) {
            if (!getArena().getSeekers().contains(pls)) {
                getArena().getHiders().add(pls);
                getArena().getTeamHiders().addPlayer(Bukkit.getPlayer(pls));
                Bukkit.getPlayer(pls).teleport(getArena().getSpawnLoc());
            } 
        }

        int hours = getArena().getBestTimer() / 3600;
        int minutes = (getArena().getBestTimer() % 3600) / 60;
        int seconds = getArena().getBestTimer() % 60;

        String bestTime = String.format("%02dh%02dmin%02ds", hours, minutes, seconds);
        getArena().sendMessage("Le meilleur temps est de " + bestTime + " détenu par " + getArena().getBestPlayer());
        
        waitingArenaTask = new WaitingArenaTask(getArena());
        waitingArenaTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (waitingArenaTask != null) waitingArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new WaitingListenerProvider(getArena());
    }

}