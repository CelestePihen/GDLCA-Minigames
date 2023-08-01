package fr.cel.eldenrpg.manager.player;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.utils.FileUtils;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.*;

public class PlayerManager {

    private static EldenRPG main;

    @Getter private static final Map<UUID, ERPlayer> playerDataMap = new HashMap<>();

    public PlayerManager(EldenRPG main) {
        PlayerManager.main = main;
    }

    /**
     * Permet de récupérer le profil du joueur grâce à son UUID
     * @param playerUUID L'UUID du joueur
     * @return Retourne le profil du joueur
     */
    public ERPlayer getPlayerData(UUID playerUUID) {
        return playerDataMap.get(playerUUID);
    }

    /**
     * Permet de charger les données du profil du joueur
     * @param playerUUID L'UUID du joueur
     * @return Retourne le profil du joueur
     */
    public ERPlayer loadData(UUID playerUUID) {
        final File dataFile = new File(EldenRPG.getEldenRPG().getDataFolder() + File.separator + "playersData", playerUUID + ".json");
        final PlayerSerializationManager manager = main.getPlayerSerializationManager();

        if (dataFile.exists()) {
            return manager.deserialize(FileUtils.loadContent(dataFile));
        } else {
            ERPlayer erPlayer = new ERPlayer(Bukkit.getPlayer(playerUUID));
            FileUtils.save(dataFile, manager.serialize(erPlayer));
            return erPlayer;
        }

    }

    /**
     * Permet de sauvegarder le profil du joueur
     * @param profilePlayer Le profil du joueur
     */
    public static void saveData(ERPlayer profilePlayer) {
        final PlayerSerializationManager manager = main.getPlayerSerializationManager();
        final File dataFile = new File(EldenRPG.getEldenRPG().getDataFolder() + File.separator + "playersData", profilePlayer.getPlayerUUID() + ".json");
        final String json = manager.serialize(profilePlayer);

        FileUtils.save(dataFile, json);
    }

    /**
     * Permet de sauvegarder le profil de tous les joueurs
     */
    public static void saveDataToAll() {
        playerDataMap.values().forEach(PlayerManager::saveData);
    }

}