package fr.cel.hub.tasks;

import fr.cel.hub.Hub;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FireworkMusicEvent extends BukkitRunnable {

    private final Hub main;
    private final World world;
    private final List<Location> locations;

    public FireworkMusicEvent(Hub main) {
        this.main = main;
        this.world = Bukkit.getWorld("world");
        this.locations = loadFireworkLocation();
    }

    @Override
    public void run() {
        for (Location location : locations) {
            final Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
            final FireworkMeta fireworkMeta = firework.getFireworkMeta();

            final FireworkEffect fireworkEffect = FireworkEffect.builder().trail(true).withColor(Color.BLUE, Color.WHITE, Color.RED).with(FireworkEffect.Type.STAR).build();

            fireworkMeta.setPower(1);

            fireworkMeta.addEffects(fireworkEffect);
            firework.setFireworkMeta(fireworkMeta);
        }
    }

    private List<Location> loadFireworkLocation() {
        File file = new File(main.getDataFolder(), "locationFireworks.yml");
        List<Location> locations1 = new ArrayList<>();
        if (file.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);

                for (String str : config.getStringList("location")) {
                    locations1.add(parseStringToLoc(str));
                }
                return locations1;
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");

        double x = Double.parseDouble(parsedLoc[0]);
        double y = Double.parseDouble(parsedLoc[1]);
        double z = Double.parseDouble(parsedLoc[2]);

        return new Location(world, x, y, z);
    }
    
}