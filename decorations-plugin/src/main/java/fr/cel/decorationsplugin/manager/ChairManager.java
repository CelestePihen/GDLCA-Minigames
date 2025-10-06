package fr.cel.decorationsplugin.manager;

import fr.cel.decorationsplugin.DecorationsPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChairManager {

    private final DecorationsPlugin plugin;
    private final Map<Location, Chair> chairs;
    private final Map<UUID, Chair> seatedPlayers;

    private File configFile;
    private FileConfiguration config;

    public ChairManager(DecorationsPlugin plugin) {
        this.plugin = plugin;
        this.chairs = new ConcurrentHashMap<>();
        this.seatedPlayers = new ConcurrentHashMap<>();
        setupConfig();
    }

    /**
     * Créer le fichier de configuration pour stocker les chaises
     */
    private void setupConfig() {
        configFile = new File(plugin.getDataFolder(), "chairs.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossible de créer le fichier de configuration !");
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Ajouter une chaise
     * @param location L'emplacement de la chaise
     * @return Retourne vrai si la chaise a été ajoutée, faux s'il y a déjà une chaise à cet emplacement
     */
    public boolean addChair(Location location) {
        if (!location.getBlock().getType().name().contains("STAIRS") && location.getBlock().getType() != Material.BARRIER) return false;

        Location blockLoc = location.getBlock().getLocation();
        if (chairs.containsKey(blockLoc)) return false;

        Chair chair = new Chair(blockLoc);
        chair.createArmorStand();
        chairs.put(blockLoc, chair);
        saveChairs();
        return true;
    }

    /**
     * Supprimer une chaise
     * @param location L'emplacement de la chaise
     * @return Retourne vrai si la chaise a été supprimée, faux s'il n'y a aucune chaise à cet emplacement
     */
    public boolean removeChair(Location location) {
        Chair chair = chairs.remove(location.getBlock().getLocation());
        if (chair == null) return false;

        if (chair.isOccupied()) seatedPlayers.remove(chair.getSeatedPlayer());
        chair.removeArmorStand();
        saveChairs();
        return true;
    }

    /**
     * Faire asseoir un joueur sur une chaise
     * @param player Le joueur qui s'assoit
     * @param chairLocation L'emplacement de la chaise
     */
    public void sitPlayer(Player player, Location chairLocation) {
        Location blockLoc = chairLocation.getBlock().getLocation();
        Chair chair = chairs.get(blockLoc);

        if (chair == null) return;

        // Vérifier si le joueur est déjà assis ailleurs
        if (seatedPlayers.containsKey(player.getUniqueId())) standUpPlayer(player);

        if (chair.sitDown(player)) seatedPlayers.put(player.getUniqueId(), chair);
    }

    /**
     * Faire lever un joueur de la chaise sur laquelle il est
     * @param player Le joueur assis sur la chaise
     */
    public void standUpPlayer(Player player) {
        Chair chair = seatedPlayers.remove(player.getUniqueId());
        if (chair != null) {
            chair.standUp();
        }
    }

    /**
     *  Supprimer tous les joueurs assis
     */
    public void removeAllSeatedPlayers() {
        for (UUID uuid : new HashSet<>(seatedPlayers.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) standUpPlayer(player);
        }
    }

    /**
     * Permet de savoir s'il y a une chaise à cet emplacement
     * @param location L'emplacement, ou pas, de la chaise
     * @return Retourne vrai s'il y a une chaise à cet emplacement, faux s'il n'y en a pas
     */
    public boolean isChair(Location location) {
        return chairs.containsKey(location.getBlock().getLocation());
    }

    /**
     * Permet de savoir si le joueur est actuellement assis sur une chaise
     * @param player Le joueur assis ou pas
     * @return Retourne vrai si le joueur est assis, faux s'il n'est pas assis
     */
    public boolean isPlayerSeated(Player player) {
        return seatedPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Permet d'obtenir la chaise sur laquelle est assis un joueur
     * @param player Le joueur assis
     * @return Retourne l'instance de la chaise sur laquelle est assis le joueur
     */
    public @Nullable Chair getChairAt(Player player) {
        return seatedPlayers.get(player.getUniqueId());
    }

    /**
     * Permet d'obtenir la chaise à l'emplacement donné
     * @param location L'emplacement de la chaise
     * @return Retourne l'instance de la chaise à l'emplacement donné
     */
    public @Nullable Chair getChairAt(Location location) {
        return chairs.get(location.getBlock().getLocation());
    }

    /**
     * Donne toutes les instances des chaises dans une List
     * @return Retourne une liste avec toutes les instances des chaises
     */
    public Set<Chair> getAllChairs() {
        return new HashSet<>(chairs.values());
    }

    /**
     * Donne le nombre de chaises
     * @return Retourne le nombre de chaises
     */
    public int getChairCount() {
        return chairs.size();
    }

    /**
     * Permet de charger les chaises à partir du fichier de configuration
     */
    public void loadChairs() {
        chairs.clear();
        seatedPlayers.clear();

        if (!config.contains("chairs")) {
            plugin.getLogger().warning("Le fichier chairs.yml ne contient pas de section chairs");
            return;
        }

        for (String key : config.getConfigurationSection("chairs").getKeys(false)) {
            try {
                String worldName = config.getString("chairs." + key + ".world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("Monde introuvable pour la chaise : " + key + " dans le monde " + worldName);
                    continue;
                }

                int x = config.getInt("chairs." + key + ".x");
                int y = config.getInt("chairs." + key + ".y");
                int z = config.getInt("chairs." + key + ".z");
                String chairIdStr = config.getString("chairs." + key + ".id");

                Location location = new Location(world, x, y, z);
                UUID chairId = chairIdStr != null ? UUID.fromString(chairIdStr) : UUID.randomUUID();

                Chair chair = new Chair(location, chairId);

                // on vérifie si la chaise a bien un Armor Stand attribué
                findExistingArmorStand(chair);

                if (chair.getArmorStand() == null) {
                    chair.createArmorStand();
                } else {
                    chair.getArmorStand().getPassengers().clear();
                }

                chairs.put(location, chair);
            } catch (Exception e) {
                plugin.getLogger().warning("Erreur lors du chargement de la chaise : " + key + " - " + e.getMessage());
            }
        }

        Bukkit.getConsoleSender().sendMessage(DecorationsPlugin.getPrefix().append(Component.text(chairs.size() + " chaises chargées !")));
    }

    /**
     * Permet de rechercher un ArmorStand existant pour une chaise
     * @param chair L'instance de la chaise
     */
    private void findExistingArmorStand(Chair chair) {
        Location searchLoc = chair.getLocation().add(0.5, 0.3, 0.5);

        for (Entity entity : searchLoc.getWorld().getNearbyEntities(searchLoc, 1, 1, 1)) {
            if (entity instanceof ArmorStand armorStand) {
                // vérification avec le Metadata
                if (armorStand.hasMetadata("stairchair")) {
                    String metaId = armorStand.getMetadata("stairchair").getFirst().asString();
                    if (metaId.equals(chair.getChairId().toString())) {
                        chair.setArmorStand(armorStand);
                        return;
                    }
                }
                // vérification par nom custom
                else if (armorStand.getCustomName() != null && armorStand.getCustomName().equals("Chair_" + chair.getChairId().toString())) {
                    chair.setArmorStand(armorStand);
                    return;
                }
            }
        }
    }

    /**
     * Sauvegarde le fichier de configuration
     */
    public void saveChairs() {
        config.set("chairs", null); // Enlève toutes les chaises du fichier

        int i = 0;
        for (Chair chair : chairs.values()) {
            String key = "chair" + i++;
            Location loc = chair.getLocation();

            config.set("chairs." + key + ".world", loc.getWorld().getName());
            config.set("chairs." + key + ".x", loc.getBlockX());
            config.set("chairs." + key + ".y", loc.getBlockY());
            config.set("chairs." + key + ".z", loc.getBlockZ());
            config.set("chairs." + key + ".id", chair.getChairId().toString());
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde de chairs.yml : " + e.getMessage());
        }
    }

    /**
     * Permet de recharger la configuration
     */
    public void reload() {
        removeAllSeatedPlayers();

        // Supprimer les anciens ArmorStands
        for (Chair chair : chairs.values()) {
            chair.removeArmorStand();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        loadChairs();
    }

}