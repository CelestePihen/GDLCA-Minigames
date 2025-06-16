package fr.cel.decorationsplugin.manager;

import fr.cel.decorationsplugin.DecorationsPlugin;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public final class DecorationsManager {

    private final fr.cel.decorationsplugin.DecorationsPlugin main;
    private final Map<String, Decoration> decorations = new HashMap<>();

    public DecorationsManager(fr.cel.decorationsplugin.DecorationsPlugin main) {
        this.main = main;
        loadAll();
    }

    public void loadAll() {
        decorations.clear();

        File folder = new File(main.getDataFolder(), "decorations");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            loadDecoration(file);
        }

        Bukkit.getConsoleSender().sendMessage(DecorationsPlugin.getPrefix() + ChatUtility.format(decorations.size() + " décorations chargées", ChatUtility.WHITE));
    }

    private void loadDecoration(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            String name = file.getName().replace(".yml", "");
            String displayName = config.getString("displayName");
            int sizeX = config.getInt("size.x");
            int sizeY = config.getInt("size.y");
            int sizeZ = config.getInt("size.z");
            boolean sittable = config.getBoolean("sittable");

            decorations.put(name, new Decoration(name, displayName, sizeX, sizeY, sizeZ, sittable));
        } catch (Exception e) {
            Bukkit.getLogger().warning("Erreur dans le fichier de décoration " + file.getName() + " : " + e.getMessage());
        }
    }

    public Decoration getFromItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return null;

        NamespacedKey itemModel = item.getItemMeta().getItemModel();
        if (itemModel == null || !itemModel.getNamespace().equals("gdlca")) return null;

        return decorations.get(itemModel.getKey());
    }

}