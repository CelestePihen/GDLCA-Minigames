package fr.floppa.jobs.job.alchimiste;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Alchimiste {

    private int exp;
    private int level;

    private final int[] paliersXP = {
            0, 280, 550, 825, 1025,                      // niveau 1 - 5
            1200, 1890, 2100, 2500, 3080,                // niveau 6 - 10
            3625, 4205, 4989, 5555, 6025,                // niveau 11 - 15
            8000, 10000, 12000, 14000, 17000,            // niveau 16 - 20
            20000, 23000, 25000, 30000, 39000,           // niveau 21 - 25
            40000, 47000, 54000, 60000, 75000            // niveau 26 - 30
    };

    public Alchimiste(int exp, int level) {
        this.exp = exp;
        this.level = level;
    }

    public void ajouterExp(Player player, int xp) {
        this.exp += xp;
        verifierNiveau(player);
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

    private void verifierNiveau(Player player) {
        for (int i = 1; i < this.paliersXP.length; i++) {
            if (exp >= this.paliersXP[i] && this.level < i + 1) {
                this.level = i + 1;
                evenementNiveauAtteint(player, this.level);
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
        Bukkit.broadcastMessage("§6" + player.getName() + " a atteint le niveau d'Alchimiste " + level + " !");

        switch (level) {
            case 5:
                player.sendMessage("Vous avez débloqué la Poudre à canon !");
                break;
            case 10:
                player.sendMessage("Vous avez débloqué la Tranche de pastèque scintillante !");
                break;
            case 15:
                player.sendMessage("Vous avez débloqué les Potions jetables !");
                break;
            case 20:
                player.sendMessage("Vous avez débloqué les Potions persistantes !");
                break;
            case 25:
                player.sendMessage("Vous avez débloqué les Yeux d'araignée fermentés !");
                break;
            case 30:
                player.sendMessage("Vous avez gagné l'effet Vision Nocturne permanent !");
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 0, false, false));
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "Alchimiste: " + "niveau=" + level + ", expérience=" + exp;
    }

}