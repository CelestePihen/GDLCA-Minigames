package fr.cel.hub.manager.event.winter2025;

import fr.cel.hub.Hub;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeadManager {

    @NotNull private final Hub main;
    @Nullable private final File headFile;
    @NotNull private final YamlConfiguration headConfig = new YamlConfiguration();

    @Getter @Setter private Map<Location, Integer> headLocations = new HashMap<>();

    public HeadManager(@NotNull Hub main) {
        this.main = main;
        this.headFile = new File(main.getDataFolder(), "heads.yml");
        loadHeadConfig();
    }

    public void loadHeadConfig() {
        headLocations.clear();

        if (!headFile.exists()) {
            try {
                headFile.createNewFile();
            } catch (IOException e) {
                main.getComponentLogger().error(Hub.getPrefix().append(Component.text("Error when creating heads.yml file: " + e.getMessage(), NamedTextColor.RED)));
            }
        }

        if (headFile.exists()) {
            try {
                headConfig.load(headFile);
                headLocations = populateHeadLocations();
            } catch (Exception e) {
                main.getComponentLogger().error(Hub.getPrefix().append(Component.text("Error when loading heads.yml file: " + e.getMessage(), NamedTextColor.RED)));
            }
        }
    }

    public Map<Location, Integer> populateHeadLocations() {
        Map<Location, Integer> locations = new HashMap<>();

        for (String str : headConfig.getStringList("heads")) {
            String[] parts = str.split(",");
            if (parts.length < 3) continue;

            try {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double z = Double.parseDouble(parts[2]);
                locations.put(new Location(Bukkit.getWorlds().getFirst(), x, y, z), Integer.parseInt(str));
            } catch (NumberFormatException e) {
                main.getComponentLogger().error(Hub.getPrefix().append(Component.text("Error when reading a head location in heads.yml file", NamedTextColor.RED)));
            }
        }

        return locations;
    }

}
