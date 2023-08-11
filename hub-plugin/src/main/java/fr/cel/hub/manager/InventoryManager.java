package fr.cel.hub.manager;

import fr.cel.hub.Hub;
import fr.cel.hub.inventory.*;
import fr.cel.hub.inventory.cachecache.CCV2Inventory;
import fr.cel.hub.inventory.cachecache.CacheCacheInventory;
import fr.cel.hub.inventory.event.EventInventory;
import fr.cel.hub.inventory.event.MusicInventory;
import fr.cel.hub.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {

    private final Hub main;
    @Getter private final Map<String, AbstractInventory> inventories = new HashMap<>();

    public InventoryManager(Hub main) {
        this.main = main;
    }

    public Inventory getInventory(String name) {
        return inventories.get(name).getInv();
    }

    public void loadInventories() {
        inventories.put("minigames", new MinigamesInventory(main));

        inventories.put("cachecache", new CacheCacheInventory(main));
        inventories.put("ccMoulin", new CCV2Inventory("Moulin", Material.WHEAT, main));
        inventories.put("ccSteampunk", new CCV2Inventory("Steampunk", Material.WAXED_COPPER_BLOCK, main));
        inventories.put("ccMarais", new CCV2Inventory("Marais", Material.SLIME_BALL, main));

        inventories.put("valocraft", new ValocraftInventory(main));
        inventories.put("pvp", new PVPInventory(main));
//        inventories.put("parkour", ...);

        inventories.put("event", new EventInventory(main));
        inventories.put("music", new MusicInventory(main));

        inventories.values().forEach(AbstractInventory::createInventory);
        inventories.keySet().forEach(str -> Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[Hub] Chargement de l'inventaire " + str)));
    }

}