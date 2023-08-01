package fr.cel.hub.tasks;

import fr.cel.hub.Hub;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FireworkMusicEvent extends BukkitRunnable {

    private final Hub main;
    private Location location;
    private World world;

    public FireworkMusicEvent(Hub main) {
        this.main = main;
        this.world = Bukkit.getWorld("world");
        this.location = new Location(world, 280.920, 65, 62.415);
    }

    @Override
    public void run() {
        main.getPlayerManager().getPlayersInHub().forEach(uuid -> {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl == null) return;
            pl.sendMessage(Component.text("Imaginez qu'il y a des feux d'artifices partout dans le hub."));
            world.spawnEntity(location, EntityType.FIREWORK);
        });
    }
    
}