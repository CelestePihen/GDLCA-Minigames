package fr.cel.halloween.map.state.game;

import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.providers.StateListenerProvider;
import fr.cel.halloween.map.providers.game.WaitingListenerProvider;
import fr.cel.halloween.map.state.MapState;
import fr.cel.halloween.map.timer.game.WaitingMapTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.UUID;

@Getter
public class WaitingMapState extends MapState {

    private WaitingMapTask waitingArenaTask;

    public WaitingMapState(HalloweenMap map) {
        super("En partie - Avant l'arrivée des chercheurs", map);
    }

    @Override
    public void onEnable(HalloweenEvent main) {
        super.onEnable(main);

        Collections.shuffle(getMap().getPlayers());

        UUID randomUUID = getMap().getPlayers().getFirst();
        Player pl = Bukkit.getPlayer(randomUUID);

        if (pl.getName().equals(getMap().getLastTracker())) {
            randomUUID = getMap().getPlayers().get(1);
            pl = Bukkit.getPlayer(randomUUID);
        }

        getMap().setLastTracker(pl.getName());
        getMap().becomeSeeker(pl);
        pl.teleport(getMap().getWaitingLoc());

        for (UUID pls : getMap().getPlayers()) {
            if (!getMap().getTracker().contains(pls)) {
                getMap().getSouls().add(pls);

                Player player = Bukkit.getPlayer(pls);
                getMap().getTeamSouls().addPlayer(player);
                player.teleport(getMap().getSpawnLoc());
            }
        }

        giveBlindness();

        waitingArenaTask = new WaitingMapTask(getMap());
        waitingArenaTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (waitingArenaTask != null) waitingArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new WaitingListenerProvider(getMap());
    }

    private void giveBlindness() {
        for (UUID uuid : getMap().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, false, false));
        }
    }

}