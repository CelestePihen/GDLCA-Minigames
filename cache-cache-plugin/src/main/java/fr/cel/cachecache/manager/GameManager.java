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

public class GameManager {
    
    @Getter private final CacheCache main;
    @Getter private ArenaManager arenaManager;
    @Getter private PlayerManager playerManager;

    @Getter private static GameManager gameManager;

    @Getter private static String prefix = ChatUtility.format("&6[Cache-Cache] &f");

    @Getter private static List<GroundItem> groundItems = new ArrayList<>();

    public GameManager(CacheCache main) {
        addGroundItems();
        gameManager = this;
        this.main = main;
        this.arenaManager = new ArenaManager(main);
        this.playerManager = Hub.getHub().getPlayerManager();
    }

    /**
     * Permet de recharger les fichiers du plugin pour pouvoir mettre de nouvelles arènes
     */
    public void reloadArenaManager() {
        this.arenaManager = new ArenaManager(main);
    }

    // Permet d'ajouter les items qui tombent au sol
    private void addGroundItems() {
        groundItems.add(new SpeedItem());
        groundItems.add(new BlindnessItem());
        groundItems.add(new SoundCatItem());
        groundItems.add(new CursedHornItem());
        groundItems.add(new ChangePositionItem());
    }

}