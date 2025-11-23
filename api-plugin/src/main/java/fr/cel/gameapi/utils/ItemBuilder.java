package fr.cel.gameapi.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class ItemBuilder {

    private final ItemStack is;
    private Map<String, Object> components = new LinkedHashMap<>();

    /**
     * Create a new ItemBuilder from scratch.
     * @param m The material to create the ItemBuilder with.
     */
    public ItemBuilder(Material m) {
        this(m, 1);
    }

    /**
     * Create a new ItemBuilder over an existing itemstack.
     * @param is The itemstack to create the ItemBuilder over.
     */
    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    /**
     * Create a new ItemBuilder from scratch.
     * @param m The material of the item.
     * @param amount The amount of the item.
     */
    public ItemBuilder(Material m, int amount) {
        is = ItemStack.of(m, amount);
    }

    /**
     * Clone the ItemBuilder into a new one.
     * @return The cloned instance.
     */
    public ItemBuilder clone() {
        return new ItemBuilder(is.clone());
    }

    /**
     * Change the durability of the item.
     * @param dur The durability to set it to.
     */
    public ItemBuilder setDurability(int dur) {
        editMeta(Damageable.class, damageable -> damageable.setDamage(dur));
        return this;
    }

    /**
     * Can put or remove the unbreakable state on the item
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        editMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
        return this;
    }

    /**
     * Put the unbreakable state on the item
     */
    public ItemBuilder setUnbreakable() {
        editMeta(itemMeta -> itemMeta.setUnbreakable(true));
        return this;
    }

    /**
     * Set the item name of the item.
     * @param text The name to change it to with a Component.
     */
    public ItemBuilder itemName(Component text) {
        editMeta(itemMeta -> itemMeta.itemName(text));
        return this;
    }

    /**
     * Set the custom name of the item.
     * @param text The name to change it to with a Component.
     */
    public ItemBuilder customName(Component text) {
        editMeta(itemMeta -> itemMeta.customName(text));
        return this;
    }

    /**
     * Add an unsafe enchantment.
     * @param enchantment The enchantment to add.
     * @param level The level to put the enchantment on.
     */
    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Remove a certain enchant from the item.
     * @param enchantment The enchantment to remove
     */
    public ItemBuilder removeEnchantment(Enchantment enchantment){
        is.removeEnchantment(enchantment);
        return this;
    }

    /**
     * Set the skull owner for the item. Works on skulls only.
     * You have to use Bukkit.createProfile(uuid) or Bukkit.createProfile(name)
     * @param playerProfile The playerProfile.
     */
    public ItemBuilder setSkullOwner(PlayerProfile playerProfile) {
        editMeta(SkullMeta.class, skullMeta -> skullMeta.setPlayerProfile(playerProfile));
        return this;
    }

    /**
     * Add an enchant to the item.
     * @param enchantment The enchantment to add
     * @param level The level
     */
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Add multiple enchants at once.
     * @param enchantments The enchants to add.
     */
    public ItemBuilder addEnchants(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lores The lore to set it to.
     */
    public ItemBuilder lore(Component... lores) {
        List<Component> loreList = new ArrayList<>(lores.length);
        for (Component c : lores) {
            loreList.add(c.colorIfAbsent(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        }

        editMeta(itemMeta -> itemMeta.lore(loreList));
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lores The lore to set it to.
     */
    public ItemBuilder lore(List<Component> lores) {
        List<Component> loreList = new ArrayList<>(lores.size());
        for (Component c : lores) {
            loreList.add(c.colorIfAbsent(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        }

        editMeta(itemMeta -> itemMeta.lore(loreList));
        return this;
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     */
    public ItemBuilder addLoreLine(Component line) {
        editMeta(im -> {
            List<Component> lore = new ArrayList<>();
            if (im.hasLore() && im.lore() != null) lore = im.lore();

            if (lore != null) lore.add(line.colorIfAbsent(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            im.lore(lore);
        });
        return this;
    }

    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
     * @param color The color to set it to.
     */
    public ItemBuilder setLeatherArmorColor(Color color) {
        editMeta(LeatherArmorMeta.class, leatherArmorMeta -> leatherArmorMeta.setColor(color));
        return this;
    }

    /**
     * Add the ItemFlag to the item
     * @param itemFlag The item flags to add
     */
    public ItemBuilder addItemFlags(ItemFlag itemFlag) {
        editMeta(im -> im.addItemFlags(itemFlag));
        return this;
    }

    /**
     * Add all ItemFlags to the item
     */
    public ItemBuilder addAllItemFlags() {
        editMeta(im -> im.addItemFlags(ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_ARMOR_TRIM,
                ItemFlag.HIDE_STORED_ENCHANTS));
        return this;
    }

    /**
     * Hide the tooltip of the item.
     */
    public ItemBuilder hideTooltip() {
        editMeta(im -> im.setHideTooltip(true));
        return this;
    }

    /**
     * Sets the custom model to the item
     * @param itemModel The item model to set
     */
    public ItemBuilder setItemModel(String itemModel) {
        editMeta(im -> im.setItemModel(NamespacedKey.fromString("gdlca:" + itemModel)));
        return this;
    }

    /**
     * Sets the custom model to the item
     * @param itemModel The item model to set
     */
    public ItemBuilder setItemModel(NamespacedKey itemModel) {
        editMeta(im -> im.setItemModel(itemModel));
        return this;
    }

    /**
     * Make the item glow.
     */
    public ItemBuilder setGlow() {
        setGlow(true);
        return this;
    }

    /**
     * Make the item glow or not.
     * @param glow Whether the item should glow or not.
     */
    public ItemBuilder setGlow(boolean glow) {
        is.editMeta(itemMeta -> itemMeta.setEnchantmentGlintOverride(glow));
        return this;
    }

    /**
     * Edits the item meta with a specific meta type.
     *
     * @param metaClass The meta class type
     * @param consumer The consumer to edit the meta
     */
    public <T extends ItemMeta> ItemBuilder editMeta(Class<T> metaClass, Consumer<T> consumer) {
        is.editMeta(metaClass, consumer);
        return this;
    }

    /**
     * Edits the item meta
     *
     * @param consumer The consumer to edit the meta
     */
    public ItemBuilder editMeta(Consumer<ItemMeta> consumer) {
        is.editMeta(consumer);
        return this;
    }

    /**
     * Add a component to the item.
     * @param component The component name.
     * @param value The value of the component.
     */
    public ItemBuilder addComponent(String component, Object value) {
        components.put(component, value);
        return this;
    }

    /**
     * Hide specific components from the tooltip.
     * @param componentNames The component names to hide.
     */
    public ItemBuilder hideComponents(String... componentNames) {
        List<String> quoted = Arrays.stream(componentNames)
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.toList());

        components.put("tooltip_display", Map.of(
                "hidden_components", "[" + String.join(",", quoted) + "]"
        ));
        return this;
    }

    /**
     * Get the itemstack from the ItemBuilder.
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    public ItemStack toItemStack() {
        if (components.isEmpty()) return is;

        StringBuilder componentString = new StringBuilder();

        String metaComponents = is.getItemMeta().getAsComponentString();
        if (!metaComponents.equals("[]")) {
            componentString.append(metaComponents, 1, metaComponents.length() - 1);
        }

        for (Map.Entry<String, Object> entry : components.entrySet()) {
            if (!componentString.isEmpty()) {
                componentString.append(",");
            }

            componentString.append(entry.getKey()).append("=").append(formatComponentValue(entry.getValue()));
        }

        String itemString = is.getType().getKey() + "[" + componentString + "]";
        return Bukkit.getItemFactory().createItemStack(itemString);
    }

    /**
     * Format a component value into a string.
     * @param value The value to format.
     * @return The formatted value.
     */
    private String formatComponentValue(Object value) {
        if (value instanceof Map<?, ?> map) {
            return "{" + map.entrySet().stream()
                    .map(e -> e.getKey() + ":" + formatComponentValue(e.getValue()))
                    .collect(Collectors.joining(",")) + "}";
        }

        return value.toString();
    }

}