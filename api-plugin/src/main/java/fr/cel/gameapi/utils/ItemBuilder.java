package fr.cel.gameapi.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ItemBuilder {

    private final ItemStack is;

    /**
     * Create a new ItemBuilder from scratch.
     * @param m The material to create the ItemBuilder with.
     */
    public ItemBuilder(Material m){
        this(m, 1);
    }

    /**
     * Create a new ItemBuilder over an existing itemstack.
     * @param is The itemstack to create the ItemBuilder over.
     */
    public ItemBuilder(ItemStack is){
        this.is = is;
    }

    /**
     * Create a new ItemBuilder from scratch.
     * @param m The material of the item.
     * @param amount The amount of the item.
     */
    public ItemBuilder(Material m, int amount){
        is = new ItemStack(m, amount);
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
        is.editMeta(Damageable.class, damageable -> damageable.setDamage(dur));
        return this;
    }

    /**
     * Can put or remove the unbreakable state on the item
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        is.editMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
        return this;
    }

    /**
     * Put the unbreakable state on the item
     */
    public ItemBuilder setUnbreakable() {
        is.editMeta(itemMeta -> itemMeta.setUnbreakable(true));
        return this;
    }

    /**
     * Set the display name of the item.
     * @param text The name to change it to with a String.
     * @deprecated
     */
    @Deprecated( since = "1.3", forRemoval = true)
    public ItemBuilder setDisplayName(String text) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatUtility.format(text));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the display name of the item.
     * @param text The name to change it to with a Component.
     */
    public ItemBuilder displayName(Component text) {
        is.editMeta(itemMeta -> itemMeta.displayName(text));
        return this;
    }

    /**
     * Set the itemname of the item.
     * @param text The name to change it to with a String.
     * @deprecated
     */
    @Deprecated(since = "1.3", forRemoval = true)
    public ItemBuilder setItemName(String text) {
        ItemMeta im = is.getItemMeta();
        im.setItemName(ChatUtility.format(text));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the itemname of the item.
     * @param text The name to change it to with a Component.
     */
    public ItemBuilder itemName(Component text) {
        is.editMeta(itemMeta -> itemMeta.itemName(text));
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
        is.editMeta(SkullMeta.class, skullMeta -> skullMeta.setPlayerProfile(playerProfile));
        return this;
    }

    /**
     * Add an enchant to the item.
     * @param enchantment The enchantment to add
     * @param level The level
     */
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta im = is.getItemMeta();
        im.addEnchant(enchantment, level, true);
        is.setItemMeta(im);
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
    @Deprecated(since = "1.3", forRemoval = true)
    public ItemBuilder setLore(String... lores) {
        ItemMeta im = is.getItemMeta();
        im.setLore(Arrays.asList(lores));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lores The lore to set it to.
     */
    public ItemBuilder lore(Component... lores) {
        is.editMeta(itemMeta -> itemMeta.lore(List.of(lores)));
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lores The lore to set it to.
     */
    @Deprecated(since = "1.3", forRemoval = true)
    public ItemBuilder setLore(List<String> lores) {
        is.editMeta(itemMeta -> itemMeta.setLore(lores));
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lores The lore to set it to.
     */
    public ItemBuilder lore(List<Component> lores) {
        is.editMeta(itemMeta -> itemMeta.lore(lores));
        return this;
    }

    /**
     * Remove a lore line.
     * @param line The line to remove.
     */
    @Deprecated(since = "1.3", forRemoval = true)
    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();

        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line)) return this;

        lore.remove(line);
        im.setLore(lore);

        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     * @param index The index of the lore line to remove.
     */
    @Deprecated(since = "1.3", forRemoval = true)
    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());

        if (index < 0 || index > lore.size()) return this;

        lore.remove(index);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     */
    @Deprecated(since = "1.3", forRemoval = true)
    public ItemBuilder addLoreLine(String line) {
        ItemMeta im = is.getItemMeta();

        List<String> lore = new ArrayList<>();
        if (im.hasLore() && im.getLore() != null) lore = new ArrayList<>(im.getLore());

        lore.add(line);
        im.setLore(lore);

        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     */
    public ItemBuilder addLoreLine(Component line) {
        is.editMeta(im -> {
            List<Component> lore = new ArrayList<>();
            if (im.hasLore() && im.lore() != null) lore = im.lore();

            if (lore != null) lore.add(line.decoration(TextDecoration.ITALIC, false));
            im.lore(lore);
        });
        return this;
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     * @param pos The index of where to put it.
     */
    @Deprecated(since = "1.3", forRemoval = true)
    public ItemBuilder addLoreLine(String line, int pos) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
     * @param color The color to set it to.
     */
    public ItemBuilder setLeatherArmorColor(Color color) {
        ItemMeta im = is.getItemMeta();
        if (im instanceof LeatherArmorMeta lam) {
            lam.setColor(color);
            is.setItemMeta(im);
        }
        return this;
    }

    /**
     * Add the ItemFlag to the item
     * @param itemFlag The item flags to add
     */
    public ItemBuilder addItemFlags(ItemFlag itemFlag) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(itemFlag);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add all ItemFlags to the item
     */
    public ItemBuilder addAllItemFlags() {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_ARMOR_TRIM,
                ItemFlag.HIDE_STORED_ENCHANTS);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Hide the tooltip of the item.
     */
    public ItemBuilder hideTooltip() {
        ItemMeta im = is.getItemMeta();
        im.setHideTooltip(true);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Sets the custom model to the item
     * @param itemModel The item model to set
     */
    public ItemBuilder setItemModel(String itemModel) {
        ItemMeta im = is.getItemMeta();
        im.setItemModel(NamespacedKey.fromString("gdlca:" + itemModel));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Sets the custom model to the item
     * @param itemModel The item model to set
     */
    public ItemBuilder setItemModel(NamespacedKey itemModel) {
        ItemMeta im = is.getItemMeta();
        im.setItemModel(itemModel);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Edits the item meta with a specific meta type.
     *
     * @param metaClass The meta class type
     * @param consumer The consumer to edit the meta
     * @return The ItemBuilder instance for method chaining.
     */
    public <T extends ItemMeta> ItemBuilder editMeta(Class<T> metaClass, Consumer<T> consumer) {
        is.editMeta(metaClass, consumer);
        return this;
    }

    /**
     * Get the itemstack from the ItemBuilder.
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    public ItemStack toItemStack(){
        return is;
    }

}