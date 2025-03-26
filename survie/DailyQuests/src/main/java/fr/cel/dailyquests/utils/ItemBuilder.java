package fr.cel.dailyquests.utils;

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
import org.bukkit.profile.PlayerProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Easily create itemstacks, without messing your hands.
 * <i>Note that if you do use this in one of your projects, leave this notice.</i>
 * <i>Please do credit me if you do use this in one of your projects.</i>
 * @author NonameSL
 */

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
        ItemMeta meta = is.getItemMeta();
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(dur);
            is.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Put unbreakable on the item
     */
    public ItemBuilder setUnbreakable() {
        ItemMeta im = is.getItemMeta();
        im.setUnbreakable(true);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the displayname of the item.
     * @param text The name to change it to.
     */
    public ItemBuilder setDisplayName(String text){
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatUtility.format(text));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add an unsafe enchantment.
     * @param enchantment The enchantment to add.
     * @param level The level to put the enchantment on.
     */
    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level){
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
        if (is.getItemMeta() instanceof SkullMeta im) {
            im.setOwnerProfile(playerProfile);
            is.setItemMeta(im);
        }
        return this;
    }

    /**
     * Add an enchant to the item.
     * @param enchantment The enchantment to add
     * @param level The level
     */
    public ItemBuilder addEnchant(Enchantment enchantment, int level){
        ItemMeta im = is.getItemMeta();
        im.addEnchant(enchantment, level, true);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add multiple enchants at once.
     * @param enchantments The enchants to add.
     */
    public ItemBuilder addEnchants(Map<Enchantment, Integer> enchantments){
        is.addEnchantments(enchantments);
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    public ItemBuilder setLore(String... lore){
        ItemMeta im = is.getItemMeta();
        im.setLore(Arrays.asList(lore));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     * @param line The line to remove.
     */
    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if(!lore.contains(line))return this;
        lore.remove(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     * @param index The index of the lore line to remove.
     */
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
    public ItemBuilder addLoreLine(String line){
        ItemMeta im = is.getItemMeta();

        List<String> lore = new ArrayList<>();

        if(im.hasLore() && im.getLore() != null) lore = new ArrayList<>(im.getLore());
        lore.add(line);
        im.setLore(lore);

        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     * @param pos The index of where to put it.
     */
    public ItemBuilder addLoreLine(String line, int pos){
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

    // Added
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
        im.addItemFlags(ItemFlag.values());
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
    // Added

    /**
     * Get the itemstack from the ItemBuilder.
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    public ItemStack toItemStack(){
        return is;
    }

}