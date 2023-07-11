package fr.cel.hub.tasks;

import fr.cel.hub.Hub;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class FireworkMusicEvent extends BukkitRunnable {

    private Location location;
    private World world;

    public FireworkMusicEvent() {
        this.world = Bukkit.getWorld("world");
        this.location = new Location(world, 280.920, 65, 62.415);
    }

    @Override
    public void run() {
        Hub.getHub().getPlayerManager().getPlayersInHub().forEach(uuid -> {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl == null) return;
            pl.sendMessage(Component.text("Imaginez qu'il y a des feux d'artifices partout dans le hub."));
            Entity entity = world.spawnEntity(location, EntityType.FIREWORK);

            if (entity instanceof Firework firework) {
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
            }

        });
    }
    
}
