package fr.cel.cachecache.arena.state.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.listeners.StateListenerProvider;
import fr.cel.cachecache.arena.listeners.game.WaitingListenerProvider;
import fr.cel.cachecache.arena.state.ArenaState;
import fr.cel.cachecache.arena.timer.game.WaitingArenaTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class WaitingArenaState extends ArenaState {

    private WaitingArenaTask waitingArenaTask;

    public WaitingArenaState(CCArena arena) {
        super("En partie avant l'arrivé des chercheurs", arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

        switch (getArena().getCcMode()) {

            // TODO refaire un jour tout le système pour ne pas tomber 2 fois de suite Chercheur
            case TwoHuntersAtStart -> {
                Collections.shuffle(getArena().getPlayers());

                int index = ThreadLocalRandom.current().nextInt(getArena().getPlayers().size());
                UUID randomUUID = getArena().getPlayers().get(index);
                Player seeker = Bukkit.getPlayer(randomUUID);

                while (seeker.getName().equalsIgnoreCase(getArena().getLastHunter())) {
                    index = ThreadLocalRandom.current().nextInt(getArena().getPlayers().size());
                    randomUUID = getArena().getPlayers().get(index);
                    seeker = Bukkit.getPlayer(randomUUID);
                }

                int index2 = ThreadLocalRandom.current().nextInt(getArena().getPlayers().size());
                UUID randomUUID2 = getArena().getPlayers().get(index2);
                Player seeker2 = Bukkit.getPlayer(randomUUID2);

                while (seeker2.getName().equalsIgnoreCase(getArena().getLastHunter()) || seeker2.getName().equalsIgnoreCase(seeker.getName())) {
                    index2 = ThreadLocalRandom.current().nextInt(getArena().getPlayers().size());
                    randomUUID2 = getArena().getPlayers().get(index2);
                    seeker2 = Bukkit.getPlayer(randomUUID2);
                }

                getArena().becomeSeeker(seeker);
                seeker.teleport(getArena().getWaitingLoc());

                getArena().becomeSeeker(seeker2);
                seeker2.teleport(getArena().getWaitingLoc());
            }

            case TousContreUn -> {
                Collections.shuffle(getArena().getPlayers());

                for (int i = 0; i < getArena().getPlayers().size() - 1; i++) {
                    Player seeker = Bukkit.getPlayer(getArena().getPlayers().get(i));
                    getArena().becomeSeeker(seeker);
                    seeker.teleport(getArena().getWaitingLoc());
                }
            }

            case LoupToucheTouche -> {
                int index = ThreadLocalRandom.current().nextInt(getArena().getPlayers().size());
                UUID randomUUID = getArena().getPlayers().get(index);
                Player seeker = Bukkit.getPlayer(randomUUID);

                if (seeker.getName().equals(getArena().getLastHunter())) {
                    index = ThreadLocalRandom.current().nextInt(getArena().getPlayers().size());
                    randomUUID = getArena().getPlayers().get(index);
                    seeker = Bukkit.getPlayer(randomUUID);
                }

                getArena().setLastHunter(seeker.getName());
                getArena().getSeekers().add(seeker.getUniqueId());
                getArena().getTeamSeekers().addPlayer(seeker);

                seeker.getInventory().clear();
                getArena().giveWeapon(seeker);
                seeker.teleport(getArena().getWaitingLoc());
            }

            default -> {
                int index = ThreadLocalRandom.current().nextInt(getArena().getPlayers().size());
                UUID randomUUID = getArena().getPlayers().get(index);
                Player seeker = Bukkit.getPlayer(randomUUID);

                if (seeker.getName().equalsIgnoreCase(getArena().getLastHunter())) {
                    index = ThreadLocalRandom.current().nextInt(getArena().getPlayers().size());
                    randomUUID = getArena().getPlayers().get(index);
                    seeker = Bukkit.getPlayer(randomUUID);
                }

                getArena().setLastHunter(seeker.getName());
                getArena().becomeSeeker(seeker);
                seeker.teleport(getArena().getWaitingLoc());
            }

        }

        for (UUID pls : getArena().getPlayers()) {
            if (!getArena().getSeekers().contains(pls)) {
                Player player = Bukkit.getPlayer(pls);
                if (player == null) continue;

                getArena().getHiders().add(pls);
                getArena().getTeamHiders().addPlayer(player);
                player.teleport(getArena().getSpawnLoc());
            }
        }

        int hours = getArena().getBestTimer() / 3600;
        int minutes = (getArena().getBestTimer() % 3600) / 60;
        int seconds = getArena().getBestTimer() % 60;

        String bestTime = String.format("%02dh%02dmin%02ds", hours, minutes, seconds);
        getArena().sendMessage("Le meilleur temps est de " + bestTime + " détenu par " + getArena().getBestPlayer() + ".");

        // TODO utilité du timer ? car cette classe sert juste à désigner les cacheurs et chercheurs donc autant le mettre dans Playing ?
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