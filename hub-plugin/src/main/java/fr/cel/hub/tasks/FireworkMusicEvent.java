package fr.cel.hub.tasks;

import fr.cel.gameapi.utils.LocationUtility;
import fr.cel.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
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
    private final List<Location> locations;

    public FireworkMusicEvent(Hub main) {
        this.main = main;
        this.locations = loadFireworkLocation();
    }

    @Override
    public void run() {
        for (Location location : locations) {
            Firework firework = (Firework) Bukkit.getWorld("world").spawnEntity(location, EntityType.FIREWORK_ROCKET);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();

            FireworkEffect fireworkEffect = FireworkEffect.builder().trail(true).withColor(Color.BLUE, Color.WHITE, Color.RED).with(FireworkEffect.Type.STAR).build();
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
                    locations1.add(LocationUtility.parseStringToLoc(config, str));
                }
                return locations1;
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
}