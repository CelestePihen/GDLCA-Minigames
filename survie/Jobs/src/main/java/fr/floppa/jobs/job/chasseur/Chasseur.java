package fr.floppa.jobs.job.chasseur;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Chasseur {

    private int exp;
    private int level;

    private final int[] paliersXP = {
            0, 280, 550, 825, 1025,                         // niveau 1 - 5
            2010, 3490, 4300, 5750, 6980,                   // niveau 6 - 10
            8025, 9605, 11100, 12700, 14000,                // niveau 11 - 15
            15800, 17500, 19100, 20980, 23150,              // niveau 16 - 20
            25125, 27325, 30000, 34580, 39000,              // niveau 21 - 25
            50000, 65000, 72000, 89600, 100000              // niveau 26 - 30
    };

    public Chasseur(int exp, int level) {
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

    private void verifierNiveau(Player player) {
        for (int i = 1; i < paliersXP.length; i++) {
            if (exp >= paliersXP[i] && level < i + 1) {
                level = i + 1;
                evenementNiveauAtteint(player, level);
            }
        }
    }

    public int getXPManquant() {
        // si niveau max -> 0
        if (this.level >= this.paliersXP.length) {
            return 0;
        }

        int xpPourNiveauSuivant = this.paliersXP[this.level];
        return xpPourNiveauSuivant - this.exp;
    }

    private void evenementNiveauAtteint(Player player, int level) {
        Bukkit.broadcastMessage("§6" + player.getName() + " a atteint le niveau de Chasseur " + level + " !");

        switch (level) {
            case 5:
                player.sendMessage("§aVous avez débloqué le Bouclier !");
                break;
            case 10:
                player.sendMessage("§aVous avez débloqué l'Arc !");
                break;
            case 15:
                player.sendMessage("§aVous avez débloqué les Perles de l'Ender !");
                break;
            case 20:
                player.sendMessage("§aVous avez débloqué le Trident !");
                break;
            case 25:
                player.sendMessage("§aVous avez débloqué les Élytres !");
                break;
            case 30:
                player.sendMessage("Vous avez gagné l'effet Force 1 permanent !");
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, -1, 0, false, false));
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "Chasseur: " + "niveau=" + level + ", expérience=" + exp;
    }

}