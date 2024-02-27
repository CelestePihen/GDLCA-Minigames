package fr.cel.cachecache.manager;

import java.util.ArrayList;
import java.util.List;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.items.*;
import fr.cel.cachecache.utils.TempHubConfig;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;

@Getter
public class CCGameManager {

    private final String prefix = ChatUtility.format("&6[Cache-Cache] &r");
    private final CacheCache main;
    private final PlayerManager playerManager = GameAPI.getInstance().getPlayerManager();

    @Getter private static final List<GroundItem> groundItems = new ArrayList<>();

    @Getter private static CCGameManager gameManager;

    private ArenaManager arenaManager;

    public CCGameManager(CacheCache main) {
        gameManager = this;
        this.main = main;

        addGroundItems();
        reloadArenaManager();
    }

    /**
     * Permet de recharger/mettre à jour les fichiers du plugin
     */
    public void reloadArenaManager() {
        this.arenaManager = new ArenaManager(main);
        arenaManager.setTemporaryHub(new TempHubConfig(main).getTemporaryHub());
    }

    /**
     * Permet d'ajouter les items qui tomberont au sol
     */
    private void addGroundItems() {
        groundItems.add(new SpeedItem());
        groundItems.add(new BlindnessItem());
        groundItems.add(new SeePlayerItem());
        groundItems.add(new SoundItem());
        groundItems.add(new InvisibilityItem());
    }

}