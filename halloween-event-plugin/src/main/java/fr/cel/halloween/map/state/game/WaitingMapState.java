package fr.cel.halloween.map.state.game;

import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.providers.StateListenerProvider;
import fr.cel.halloween.map.providers.game.WaitingListenerProvider;
import fr.cel.halloween.map.state.MapState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.UUID;

@Getter
public class WaitingMapState extends MapState {

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

        getMap().setMapState(new PlayingMapState(getMap()));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new WaitingListenerProvider(getMap());
    }

    private void giveBlindness() {
        for (UUID uuid : getMap().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, false, false));
        }
    }

}