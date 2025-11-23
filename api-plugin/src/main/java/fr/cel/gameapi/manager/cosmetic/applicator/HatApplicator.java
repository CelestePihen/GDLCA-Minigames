package fr.cel.gameapi.manager.cosmetic.applicator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Applicator for hat cosmetics (items worn on the player's head)
 */
public class HatApplicator implements CosmeticApplicator {

    @Override
    public void apply(Player player, Cosmetic cosmetic) {
        player.getInventory().setHelmet(createHatItem(cosmetic));
    }

    @Override
    public void remove(Player player, Cosmetic cosmetic) {
        ItemStack currentHelmet = player.getInventory().getHelmet();
        if (currentHelmet != null && currentHelmet.getType() == Material.PAPER) player.getInventory().setHelmet(null);
    }

    @Override
    public void apply(Mannequin mannequin, Cosmetic cosmetic) {
        mannequin.getEquipment().setHelmet(createHatItem(cosmetic));
    }

    @Override
    public void remove(Mannequin mannequin, Cosmetic cosmetic) {
        ItemStack currentHelmet = mannequin.getEquipment().getHelmet();
        if (currentHelmet != null && currentHelmet.getType() == Material.PAPER) mannequin.getEquipment().setHelmet(null);
    }

    /**
     * Create an item stack for the hat cosmetic
     * @param cosmetic The cosmetic
     * @return The item stack
     */
    private ItemStack createHatItem(Cosmetic cosmetic) {
        ItemBuilder builder = new ItemBuilder(Material.PAPER).itemName(Component.text(cosmetic.getName()));

        String itemModel = parseItemModel(cosmetic.getData());
        if (itemModel != null && !itemModel.isEmpty()) {
            builder.setItemModel(itemModel);
        }

        return builder.toItemStack();
    }

    /**
     * Parse item model from cosmetic data
     * @param data The cosmetic data JSON
     * @return The item model string, or null if not found
     */
    private String parseItemModel(String data) {
        try {
            JsonObject json = JsonParser.parseString(data).getAsJsonObject();
            if (json.has("itemModel")) {
                return json.get("itemModel").getAsString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}

