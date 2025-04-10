package fr.floppa.jobs.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class JobsCommand implements CommandExecutor {

    private final Inventory inventory;

    public JobsCommand() {
        this.inventory = createInventory();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        player.openInventory(inventory);
        return false;
    }

    private Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(null, 2*9, "Paliers des Métiers");

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            Objects.requireNonNull(itemStack.getItemMeta()).setHideTooltip(true);
            inv.setItem(i, itemStack);
        }

        inv.setItem(2, item(Material.BREWING_STAND, "Alchimiste",
                List.of("Niveau 5 : Poudre à canon", "Niveau 10 : Tranche de pastèque scintillante", "Niveau 15 : Potion jetable",
                        "Niveau 20 : Potion Persistante", "Niveau 25 : Oeil d'araignée fermentée", "Niveau 30 : Vision Nocturne Permanent")));

        inv.setItem(4, item(Material.DIAMOND_SWORD, "Chasseur",
                List.of("Niveau 5 : Bouclier", "Niveau 10 : Arc", "Niveau 15 : Perle de l'Ender",
                        "Niveau 20 : Trident", "Niveau 25 : Elytres", "Niveau 30 : Force 1 Permanent")));

        inv.setItem(6, item(Material.ENCHANTING_TABLE, "Enchanteur",
                List.of("Niveau 5 : Bibliothèque", "Niveau 10 : Pupitre", "Niveau 15 : Fiole d'expérience",
                        "Niveau 20 : Enchantement Solidité", "Niveau 25 : Enchantement Raccommodage", "Niveau 30 : Résistance 1 Permanent")));

        inv.setItem(11, item(Material.GRASS_BLOCK, "Explorateur",
                List.of("Niveau 5 : Lit", "Niveau 10 : Pomme dorée enchantée", "Niveau 15 : Oeuf de Dragon",
                        "Niveau 20 : Enchantement Semelles Givrantes", "Niveau 25 : Boîte de Shulker", "Niveau 30 : Vitesse 1 Permanent")));

        inv.setItem(13, item(Material.WHEAT, "Farmeur",
                List.of("Niveau 5 : Houe en fer", "Niveau 10 : Poudre d'os", "Niveau 15 : Pomme en or",
                        "Niveau 20 : Carotte en or", "Niveau 25 : Enchantement Fortune", "Niveau 30 : Chance 1 Permanent")));

        inv.setItem(15, item(Material.DIAMOND_PICKAXE, "Mineur",
                List.of("Niveau 5 : Cuivre", "Niveau 10 : Fer", "Niveau 15 : Diamant",
                        "Niveau 20 : Netherite", "Niveau 25 : Masse", "Niveau 30 : Célérité 1 Permanent ")));

        return inv;
    }

    private ItemStack item(Material material, String name, List<String> lores) {
        ItemStack itemStack = new ItemStack(material);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setItemName(name);
        itemMeta.setLore(lores);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}