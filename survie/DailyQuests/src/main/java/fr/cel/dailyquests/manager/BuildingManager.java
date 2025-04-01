package fr.cel.dailyquests.manager;

import fr.cel.dailyquests.DailyQuests;
import fr.cel.dailyquests.utils.LocationUtility;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BuildingManager {

    private final Location buildingLoc1, buildingLoc2;
    private final Location chestsLoc1, chestsLoc2;

    private final Map<Material, Integer> requiredMaterials;
    private final List<Inventory> inventories;

    public BuildingManager(DailyQuests main) {
        this.buildingLoc1 = LocationUtility.parseConfigToLoc(main.getGlobalFileConfig(), "buildingLoc1");
        this.buildingLoc2 = LocationUtility.parseConfigToLoc(main.getGlobalFileConfig(), "buildingLoc2");

        this.chestsLoc1 = LocationUtility.parseConfigToLoc(main.getGlobalFileConfig(), "chestsLoc1");
        this.chestsLoc2 = LocationUtility.parseConfigToLoc(main.getGlobalFileConfig(), "chestsLoc2");

        this.requiredMaterials = this.calculateRequiredMaterials();
        this.inventories = this.getInventories();
    }

    public void giveBook(Player player) {
        Map<Material, Integer> availableMaterials = new HashMap<>();

        for (Inventory inventory : inventories) {
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType() != Material.AIR)
                    availableMaterials.put(item.getType(), availableMaterials.getOrDefault(item.getType(), 0) + item.getAmount());
            }
        }

        Map<Material, Integer> missingMaterials = new HashMap<>();

        for (Map.Entry<Material, Integer> entry : requiredMaterials.entrySet()) {
            Material material = entry.getKey();
            int requiredAmount = entry.getValue();
            int availableAmount = availableMaterials.getOrDefault(material, 0);

            if (availableAmount < requiredAmount)
                missingMaterials.put(material, requiredAmount - availableAmount);
        }

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.itemName(Component.text("Matériaux Manquants"));
        bookMeta.author(Component.text("L'Institution"));

        List<Component> pages = new ArrayList<>();

        if (missingMaterials.isEmpty()) {
            pages.add(Component.text("Tous les matériaux ont été acquéris."));
        } else {
            for (Map.Entry<Material, Integer> entry : missingMaterials.entrySet()) {
                Component pageContent = Component.text("- ")
                        .append(Component.text(entry.getValue() + " "))
                        .append(Component.translatable(entry.getKey().translationKey()));
                pages.add(pageContent);
            }
        }

        bookMeta.pages(pages);
        book.setItemMeta(bookMeta);

        player.getInventory().addItem(book);
    }

    public double getPercentage() {
        Map<Material, Integer> availableMaterials = calculateAvailableMaterials();

        int totalRequired = 0;
        int totalAvailable = 0;

        for (Map.Entry<Material, Integer> entry : requiredMaterials.entrySet()) {
            Material material = entry.getKey();
            int requiredAmount = entry.getValue();
            int availableAmount = availableMaterials.getOrDefault(material, 0);

            totalRequired += requiredAmount;
            totalAvailable += Math.min(requiredAmount, availableAmount);
        }

        if (totalRequired == 0) {
            return 100.0;
        }

        return (double) totalAvailable / totalRequired * 100.0;
    }

    private Map<Material, Integer> calculateAvailableMaterials() {
        Map<Material, Integer> availableMaterials = new HashMap<>();

        for (Inventory inventory : inventories) {
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType() != Material.AIR)
                    availableMaterials.put(item.getType(), availableMaterials.getOrDefault(item.getType(), 0) + item.getAmount());
            }
        }

        return availableMaterials;
    }

    private List<Inventory> getInventories() {
        int minX = Math.min(chestsLoc1.getBlockX(), chestsLoc2.getBlockX());
        int minY = Math.min(chestsLoc1.getBlockY(), chestsLoc2.getBlockY());
        int minZ = Math.min(chestsLoc1.getBlockZ(), chestsLoc2.getBlockZ());
        int maxX = Math.max(chestsLoc1.getBlockX(), chestsLoc2.getBlockX());
        int maxY = Math.max(chestsLoc1.getBlockY(), chestsLoc2.getBlockY());
        int maxZ = Math.max(chestsLoc1.getBlockZ(), chestsLoc2.getBlockZ());

        List<Inventory> inventories = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = Bukkit.getWorld("world").getBlockAt(x, y, z);

                    if (block.getType() == Material.CHEST) {
                        Chest chest = (Chest) block.getState();
                        Inventory inventory = chest.getInventory();
                        inventories.add(inventory);
                    }
                }
            }
        }

        return inventories;
    }

    private Map<Material, Integer> calculateRequiredMaterials() {
        int minX = Math.min(buildingLoc1.getBlockX(), buildingLoc2.getBlockX());
        int minY = Math.min(buildingLoc1.getBlockY(), buildingLoc2.getBlockY());
        int minZ = Math.min(buildingLoc1.getBlockZ(), buildingLoc2.getBlockZ());
        int maxX = Math.max(buildingLoc1.getBlockX(), buildingLoc2.getBlockX());
        int maxY = Math.max(buildingLoc1.getBlockY(), buildingLoc2.getBlockY());
        int maxZ = Math.max(buildingLoc1.getBlockZ(), buildingLoc2.getBlockZ());

        World world = Bukkit.getWorld("world");
        Map<Material, Integer> blockCountMap = new HashMap<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Material material = world.getBlockAt(x, y, z).getType();

                    if (material == Material.AIR || material == Material.RED_CONCRETE) continue;

                    // s'il y a des torches accrochées au mur, cela mettra WALL_TORCH
                    // sauf qu'il n'existe pas d'objet nommé WALL_TORCH donc on met TORCH à la place
                    if (material == Material.WALL_TORCH)
                        material = Material.TORCH;
                    else if (material.name().startsWith("POTTED_"))
                        material = Material.FLOWER_POT;

                    blockCountMap.put(material, blockCountMap.getOrDefault(material, 0) + 1);
                }
            }
        }

        return blockCountMap;
    }

}
