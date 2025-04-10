package fr.floppa.jobs.job.explorateur;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Explorateur {

    private int exp;
    private int level;

    private final int[] paliersXP = {
            0, 1523, 2992, 4488, 5575,                        // niveau 1 - 5
            10933, 18984, 23390, 31277, 37967,                // niveau 6 - 10
            43651, 52246, 60378, 69081, 76152,                // niveau 11 - 15
            85943, 95190, 103893, 114119, 125923,             // niveau 16 - 20
            136665, 148632, 163183, 188095, 212137,           // niveau 21 - 25
            271971, 353562, 391638, 487372, 543942            // niveau 26 - 30
    };

    public Explorateur(int exp, int level) {
        this.exp = exp;
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void ajouterExp(Player player, int xp) {
        this.exp += xp;
        verifierNiveau(player);
    }

    public int getXPManquant() {
        // si niveau max -> 0
        if (this.level >= this.paliersXP.length) {
            return 0;
        }

        int xpPourNiveauSuivant = this.paliersXP[this.level];
        return xpPourNiveauSuivant - this.exp;
    }

    private void verifierNiveau(Player player) {
        for (int i = 1; i < paliersXP.length; i++) {
            if (exp >= paliersXP[i] && level < i + 1) {
                level = i + 1;
                evenementNiveauAtteint(player, level);
            }
        }
    }

    private void evenementNiveauAtteint(Player player, int level) {
        Bukkit.broadcastMessage("§6" + player.getName() + " a atteint le niveau d'Explorateur " + level + " !");

        switch (level) {
            case 5:
                player.sendMessage("§aVous avez débloqué le Lit !");
                break;
            case 10:
                player.sendMessage("§aVous gagnez une Pomme dorée enchantée !");
                player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));
                break;
            case 15:
                player.sendMessage("§aVous avez débloqué l'Oeuf de Dragon !");
                break;
            case 20:
                player.sendMessage("§aVous avez débloqué l'enchantement Semelles givrantes !");
                break;
            case 25:
                player.sendMessage("§aVous avez débloqué la Boîte de Shulker !");
                break;
            case 30:
                player.sendMessage("Vous avez gagné l'effet Vitesse 1 permanent !");
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "Explorateur: " + "niveau=" + level + ", expérience=" + exp;
    }

}