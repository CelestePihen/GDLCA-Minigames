package fr.cel.manhunt.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.cel.manhunt.ManHunt;

public class CompassTask extends BukkitRunnable {

    private ManHunt main;

    public CompassTask(ManHunt main) {
        this.main = main;
    }

    @Override
    public void run() {
        for (Player pls : Bukkit.getOnlinePlayers()) {
            Player man = Bukkit.getPlayer(main.getMan().get(0));
            pls.setCompassTarget(man.getLocation());
        }
    }
    
}