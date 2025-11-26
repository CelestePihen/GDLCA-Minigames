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

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HeadManager {

    @NotNull private final Hub main;
    @NotNull private final File headFile;
    @NotNull private final YamlConfiguration headConfig = new YamlConfiguration();

    @Getter @Setter private Map<Location, Integer> headLocations = new HashMap<>();

    public static final String VALUE_GIFT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc0NDJmMWJlOGQzYWRlNmRmMTZjMGQxYjI0Njg1NDViNWU1MzNkZGMwNThlYTU5M2UxNzk4NmM0YTNhYjM4In19fQ==";

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
            if (parts.length < 4) continue;

            try {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double z = Double.parseDouble(parts[2]);
                int id = Integer.parseInt(parts[3]);
                locations.put(new Location(Bukkit.getWorlds().getFirst(), x, y, z), id);
            } catch (NumberFormatException e) {
                main.getComponentLogger().error(Component.text("Error when reading a head location in heads.yml file", NamedTextColor.RED));
            }
        }

        return locations;
    }

    /**
     * Obtient le prochain ID disponible (dernier ID + 1)
     * @return Le prochain ID disponible
     */
    public int getNextAvailableId() {
        if (headLocations.isEmpty()) {
            return 1;
        }

        int maxId = headLocations.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        return maxId + 1;
    }

    /**
     * Récupère tous les IDs des têtes enregistrées
     * @return Un Set contenant tous les IDs des têtes
     */
    public Set<Integer> getAllHeadIds() {
        return new HashSet<>(headLocations.values());
    }

    /**
     * Ajoute une tête à une location donnée
     * @param location La location de la tête
     * @param id L'ID de la tête
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean addHead(@NotNull Location location, int id) {
        headLocations.put(location, id);
        return saveHeads();
    }

    /**
     * Sauvegarde toutes les têtes dans le fichier heads.yml
     * @return true si la sauvegarde a réussi, false sinon
     */
    private boolean saveHeads() {
        List<String> headsList = new ArrayList<>();

        for (Map.Entry<Location, Integer> entry : headLocations.entrySet()) {
            Location loc = entry.getKey();
            int id = entry.getValue();
            String headString = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + id;
            headsList.add(headString);
        }

        headConfig.set("heads", headsList);

        try {
            headConfig.save(headFile);
            loadHeadConfig();
            return true;
        } catch (IOException e) {
            main.getComponentLogger().error(Component.text("Error when saving heads.yml file: " + e.getMessage(), NamedTextColor.RED));
            return false;
        }
    }

}
