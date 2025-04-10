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

public class ResetLevelCommand implements CommandExecutor {

    private final Jobs main;

    public ResetLevelCommand(Jobs main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player && player.isOp()) {
            Alchimiste alchimiste = main.getAlchimisteManager().getAlchimiste(player);
            alchimiste.setExp(0);
            alchimiste.setLevel(1);

            Chasseur chasseur = main.getChasseurManager().getChasseur(player);
            chasseur.setExp(0);
            chasseur.setLevel(1);

            Enchanteur enchanteur = main.getEnchanteurManager().getEnchanteur(player);
            enchanteur.setExp(0);
            enchanteur.setLevel(1);

            Explorateur explorateur = main.getExplorateurManager().getExplorateur(player);
            explorateur.setExp(0);
            explorateur.setLevel(1);

            Fermier fermier = main.getFermierManager().getFermier(player);
            fermier.setExp(0);
            fermier.setLevel(1);

            Mineur mineur = main.getMineurManager().getMineur(player);
            mineur.setExp(0);
            mineur.setLevel(1);

            player.sendMessage("§aTous vos métiers ont été réinitialisés !");
        }

        return true;
    }

}