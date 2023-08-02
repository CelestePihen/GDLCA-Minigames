package fr.cel.hub.manager;

import fr.cel.hub.Hub;
import fr.cel.hub.inventory.*;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {

    @Getter private final Map<String, AbstractInventory> inventories;
    private final Hub main;

    public InventoryManager(Hub main) {
        this.main = main;
        inventories = new HashMap<>();
    }

    public void loadInventories() {
        inventories.put("minigames", new MinigamesInventory(main));

        inventories.put("cachecache", new CacheCacheInventory(main));
        inventories.put("ccMoulin", new CCV2Inventory("Moulin", Material.WHEAT, main));
        inventories.put("ccSteampunk", new CCV2Inventory("Steampunk", Material.WAXED_COPPER_BLOCK, main));
        inventories.put("ccMarais", new CCV2Inventory("Marais", Material.SLIME_BALL, main));

        inventories.put("valocraft", new ValocraftInventory(main));
        inventories.put("pvp", new PVPInventory(main));

        inventories.values().forEach(AbstractInventory::createInventory);
        inventories.keySet().forEach(str -> Bukkit.getConsoleSender().sendMessage(Component.text("[Hub] ", NamedTextColor.GOLD).append(Component.text("Chargement de l'inventaire " + str, NamedTextColor.WHITE))));
    }

}