package fr.floppa.jobs.commands;

import fr.floppa.jobs.Jobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class KitDepartCommand implements CommandExecutor {

    private final Jobs main;

    public KitDepartCommand(Jobs main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (main.getDepartListener().getHasFinishedTutorials().get(player.getUniqueId())) return true;
        openKitDepartInventory(player);
        return true;
    }

    private void openKitDepartInventory(Player player) {
        Inventory choixClasse = Bukkit.createInventory(null, 9, "Choisissez votre kit au départ !");

        choixClasse.setItem(0, createClassItem(Material.IRON_SWORD, "Choisir le Combattant !",
                "Le combattant commence avec un supplément de cœur et une arme de départ !"));

        choixClasse.setItem(1, createClassItem(Material.MAP, "Choisir l'Aventurier !",
                "L'aventurier commence avec des objets d'exploration !"));

        choixClasse.setItem(2, createClassItem(Material.COOKED_BEEF, "Choisir le Cuisinier !",
                "Le cuisinier commence avec un gros stock de nourriture !"));

        choixClasse.setItem(3, createClassItem(Material.IRON_HOE, "Choisir le Paysan !",
                "Le paysan commence avec les nécessaires pour cultiver les terres !"));

        choixClasse.setItem(4, createClassItem(Material.IRON_AXE, "Choisir le Bûcheron !",
                "Le bûcheron commence avec les outils adaptés pour l’abattage d’arbres !"));

        choixClasse.setItem(5, createClassItem(Material.FISHING_ROD, "Choisir le Pêcheur !",
                "Le pêcheur commence avec sa bonne vieille canne qui lui porte bonheur !"));

        choixClasse.setItem(6, createClassItem(Material.ANVIL, "Choisir le Forgeron !",
                "Le forgeron commence avec une enclume et de l'XP en plus !"));

        choixClasse.setItem(7, createClassItem(Material.BOOK, "Choisir le Bibliothécaire !",
                "Le bibliothécaire commence avec un livre enchanté avec des enchantements aléatoires !"));

        choixClasse.setItem(8, createClassItem(Material.BARRIER, "Choisir l'Hardcore !",
                "Vous commencez avec un cœur (grosses paires de coui****) !"));

        player.openInventory(choixClasse);
    }

    private ItemStack createClassItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setItemName(name);
        meta.setLore(List.of(ChatColor.WHITE + lore));
        item.setItemMeta(meta);
        return item;
    }

}