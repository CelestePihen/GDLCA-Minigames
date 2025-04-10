package fr.floppa.jobs.job.alchimiste;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AlchimisteManager {

    private final Map<UUID, Alchimiste> alchimistes = new HashMap<>();

    public Alchimiste getAlchimiste(Player player) {
        return alchimistes.get(player.getUniqueId());
    }

    public void addAlchimiste(Player player, int xp, int level) {
        alchimistes.put(player.getUniqueId(), new Alchimiste(xp, level));
    }

}