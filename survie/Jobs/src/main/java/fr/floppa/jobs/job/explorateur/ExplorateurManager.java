package fr.floppa.jobs.job.explorateur;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ExplorateurManager {

    private final HashMap<UUID, Explorateur> explorateurs = new HashMap<>();

    public Explorateur getExplorateur(Player player) {
        return explorateurs.get(player.getUniqueId());
    }

    public void addExplorateur(Player player, int xp, int level){
        explorateurs.put(player.getUniqueId(), new Explorateur(xp, level));
    }

}