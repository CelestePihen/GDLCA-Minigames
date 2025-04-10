package fr.floppa.jobs.job.enchanteur;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EnchanteurManager {

    private final HashMap<UUID, Enchanteur> enchanteurs = new HashMap<>();

    public Enchanteur getEnchanteur(Player player) {
        return enchanteurs.get(player.getUniqueId());
    }

    public void addEnchanteur(Player player, int xp, int level) {
        enchanteurs.put(player.getUniqueId(), new Enchanteur(xp, level));
    }

}