package fr.floppa.jobs.job.fermier;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FermierManager {

    private final Map<UUID, Fermier> fermiers = new HashMap<>();

    public Fermier getFermier(Player player) {
        return fermiers.get(player.getUniqueId());
    }

    public void addFermier(Player player, int xp, int level){
        fermiers.put(player.getUniqueId(), new Fermier(xp, level));
    }

}