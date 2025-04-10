package fr.floppa.jobs.job.mineur;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MineurManager {

    private final HashMap<UUID, Mineur> mineurs = new HashMap<>();

    public Mineur getMineur(Player player) {
        return mineurs.get(player.getUniqueId());
    }

    public void addMineur(Player player, int xp, int level){
        mineurs.put(player.getUniqueId(), new Mineur(xp, level));
    }

}