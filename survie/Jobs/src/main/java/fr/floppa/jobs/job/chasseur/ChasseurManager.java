package fr.floppa.jobs.job.chasseur;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ChasseurManager {

    private final HashMap<UUID, Chasseur> chasseurs = new HashMap<>();

    public Chasseur getChasseur(Player player) {
        return chasseurs.get(player.getUniqueId());
    }

    public void addChasseur(Player player, int xp, int level){
        chasseurs.put(player.getUniqueId(), new Chasseur(xp, level));
    }

}