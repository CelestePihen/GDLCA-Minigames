package fr.cel.cachecache.arena.state.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.state.ArenaState;
import fr.cel.cachecache.arena.state.providers.StateListenerProvider;
import fr.cel.cachecache.arena.state.providers.game.WaitingListenerProvider;
import fr.cel.cachecache.arena.timer.game.WaitingArenaTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

@Getter
public class WaitingArenaState extends ArenaState {

    private WaitingArenaTask waitingArenaTask;

    public WaitingArenaState(CCArena arena) {
        super("En partie avant l'arrivé des chercheurs", arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

//        if (getArena().getArenaName().equalsIgnoreCase("bunker")) {
//            Block lever = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(getArena().getLeverLocation());
//            if (lever.getBlockData() instanceof Powerable powerable) {
//                powerable.setPowered(true);
//                lever.setBlockData(powerable);
//
//                Block redstoneWire = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(getArena().getRedstoneWireLocation());
//                redstoneWire.setType(Material.AIR);
//                redstoneWire.setType(Material.REDSTONE_WIRE);
//            }
//        }

        Collections.shuffle(getArena().getPlayers());

        switch (getArena().getHunterMode()) {

            case TwoHuntersAtStart -> {
                Player player = Bukkit.getPlayer(getArena().getPlayers().getFirst());
                getArena().becomeSeeker(player);
                player.teleport(getArena().getWaitingLoc());

                Player player2 = Bukkit.getPlayer(getArena().getPlayers().get(1));
                getArena().becomeSeeker(player2);
                player2.teleport(getArena().getWaitingLoc());
            }

            case TousContreUn -> {
                for (int i = 0; i < getArena().getPlayers().size() - 1; i++) {
                    Player player = Bukkit.getPlayer(getArena().getPlayers().get(i));

                    getArena().becomeSeeker(player);
                    player.teleport(getArena().getWaitingLoc());
                }
            }

            case LoupToucheTouche -> {
                UUID randomUUID = getArena().getPlayers().get(0);
                Player player = Bukkit.getPlayer(randomUUID);

                if (player.getName().equals(getArena().getLastHunter())) {
                    randomUUID = getArena().getPlayers().get(1);
                    player = Bukkit.getPlayer(randomUUID);
                }

                getArena().setLastHunter(player.getName());
                getArena().getSeekers().add(player.getUniqueId());
                getArena().getTeamSeekers().addPlayer(player);

                player.getInventory().clear();
                getArena().giveWeapon(player);
                player.teleport(getArena().getWaitingLoc());
            }

            default -> {
                UUID randomUUID = getArena().getPlayers().get(0);
                Player player = Bukkit.getPlayer(randomUUID);

                if (player.getName().equals(getArena().getLastHunter())) {
                    randomUUID = getArena().getPlayers().get(1);
                    player = Bukkit.getPlayer(randomUUID);
                }

                getArena().setLastHunter(player.getName());
                getArena().becomeSeeker(player);
                player.teleport(getArena().getWaitingLoc());
            }

        }

        for (UUID pls : getArena().getPlayers()) {
            if (!getArena().getSeekers().contains(pls)) {
                getArena().getHiders().add(pls);

                Player player = Bukkit.getPlayer(pls);
                getArena().getTeamHiders().addPlayer(player);
                player.teleport(getArena().getSpawnLoc());
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