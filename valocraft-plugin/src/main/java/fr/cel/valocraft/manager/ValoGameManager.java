package fr.cel.valocraft.manager;

import fr.cel.hub.Hub;
import fr.cel.hub.inventory.AbstractInventory;
import fr.cel.hub.manager.PlayerManager;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.inventory.SelectTeam;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class ValoGameManager {

    @Getter private static ValoGameManager gameManager;

    @Getter private final ValoCraft main;
    @Getter private final PlayerManager playerManager;
    @Getter private final String prefix = ChatUtility.format("&6[Valocraft]&r ");
    @Getter private final Map<String, AbstractInventory> inventories = new HashMap<>();

    @Getter private ArenaManager arenaManager;

    public ValoGameManager(ValoCraft main) {
        this.main = main;
        gameManager = this;
        this.arenaManager = new ArenaManager(main, this);
        this.playerManager = Hub.getHub().getPlayerManager();
    }

    public void reloadArenaManager() {
        this.arenaManager = new ArenaManager(main, this);
    }

    public void loadInventories() {
        inventories.put("selectteam", new SelectTeam(Hub.getHub()));

        inventories.values().forEach(AbstractInventory::createInventory);
        inventories.keySet().forEach(str -> Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[Valocraft] Chargement de l'inventaire " + str)));
    }

}