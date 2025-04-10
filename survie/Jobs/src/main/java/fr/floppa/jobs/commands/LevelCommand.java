package fr.floppa.jobs.commands;

import fr.floppa.jobs.Jobs;
import fr.floppa.jobs.job.alchimiste.Alchimiste;
import fr.floppa.jobs.job.chasseur.Chasseur;
import fr.floppa.jobs.job.enchanteur.Enchanteur;
import fr.floppa.jobs.job.explorateur.Explorateur;
import fr.floppa.jobs.job.fermier.Fermier;
import fr.floppa.jobs.job.mineur.Mineur;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand implements CommandExecutor {

    private final Jobs main;

    public LevelCommand(Jobs main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        Alchimiste alchimiste = main.getAlchimisteManager().getAlchimiste(player);
        Chasseur chasseur = main.getChasseurManager().getChasseur(player);
        Enchanteur enchanteur = main.getEnchanteurManager().getEnchanteur(player);
        Explorateur explorateur = main.getExplorateurManager().getExplorateur(player);
        Fermier fermier = main.getFermierManager().getFermier(player);
        Mineur mineur = main.getMineurManager().getMineur(player);

        player.sendMessage("§b"
                + alchimiste.toString()  + " §f(Prochain palier : " + alchimiste.getXPManquant()  + "XP)" + "\n§b"
                + chasseur.toString()    + " §f(Prochain palier : " + chasseur.getXPManquant()    + "XP)" + "\n§b"
                + enchanteur.toString()  + " §f(Prochain palier : " + enchanteur.getXPManquant()  + "XP)" + "\n§b"
                + explorateur.toString() + " §f(Prochain palier : " + explorateur.getXPManquant() + "XP)" + "\n§b"
                + fermier.toString()     + " §f(Prochain palier : " + fermier.getXPManquant()     + "XP)" + "\n§b"
                + mineur.toString()      + " §f(Prochain palier : " + mineur.getXPManquant()      + "XP)" + "\n"
        );

        return false;
    }

}