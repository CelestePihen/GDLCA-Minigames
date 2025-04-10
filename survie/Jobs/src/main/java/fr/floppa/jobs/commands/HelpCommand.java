package fr.floppa.jobs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§bCommandes pour le plugin Métiers");
        sender.sendMessage("/help : Affiche les commandes du plugin.");
        sender.sendMessage("/jafar : Jafar");
        sender.sendMessage("/kitdepart : Permet de choisir son kit de départ quand on est dans le tutoriel.");
        sender.sendMessage("/level | /niveau : Affiche ses niveaux et expériences de ses métiers ainsi que l'expérience manquante avant d'atteindre le prochain niveau.");
        sender.sendMessage("/jobs | /metiers : Affiche les différents paliers des métiers avec les objets déblocables.");
        return false;
    }

}