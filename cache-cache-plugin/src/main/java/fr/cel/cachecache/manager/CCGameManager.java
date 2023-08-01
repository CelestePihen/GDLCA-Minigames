package fr.cel.cachecache.manager;

import java.util.ArrayList;
import java.util.List;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.arena.ArenaManager;
import fr.cel.cachecache.manager.groundItems.BlindnessItem;
import fr.cel.cachecache.manager.groundItems.ChangePositionItem;
import fr.cel.cachecache.manager.groundItems.CursedHornItem;
import fr.cel.cachecache.manager.groundItems.SoundCatItem;
import fr.cel.cachecache.manager.groundItems.SpeedItem;
import fr.cel.hub.Hub;
import fr.cel.hub.manager.PlayerManager;
import fr.cel.hub.utils.ChatUtility;
import lombok.Getter;

public class CCGameManager {
    
    @Getter private final CacheCache main;
    @Getter private final PlayerManager playerManager;
    @Getter private static final List<GroundItem> groundItems = new ArrayList<>();

    @Getter private ArenaManager arenaManager;

    @Getter private static CCGameManager gameManager;

    @Getter private final String prefix = ChatUtility.format("&6[Cache-Cache] &f");

    public CCGameManager(CacheCache main) {
        addGroundItems();
        gameManager = this;
        this.main = main;
        this.arenaManager = new ArenaManager(main);
        this.playerManager = Hub.getHub().getPlayerManager();
    }

    /**
     * Permet de recharger/mettre à jour les fichiers du plugin
     */
    public void reloadArenaManager() {
        this.arenaManager = new ArenaManager(main);
    }

    /**
     * Permet d'ajouter les items qui tomberont au sol
     */
    private void addGroundItems() {
        groundItems.add(new SpeedItem());
        groundItems.add(new BlindnessItem());
        groundItems.add(new ChangePositionItem());
        groundItems.add(new SoundCatItem());
        groundItems.add(new CursedHornItem());
    }

}