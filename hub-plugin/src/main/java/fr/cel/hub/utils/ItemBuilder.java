package fr.cel.hub.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Easily create itemstacks, without messing your hands.
 * <i>Note that if you do use this in one of your projects, leave this notice.</i>
 * <i>Please do credit me if you do use this in one of your projects.</i>
 * @author NonameSL
 */

public class ItemBuilder {

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
     * Create a new ItemBuilder from scratch.
     * @param m The material of the item.
     * @param amount The amount of the item.
     * @param durability The durability of the item.
    public ItemBuilder(Material m, int amount, byte durability){
        is = new ItemStack(m, amount, durability);
    }
     */

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
        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(dur);
            is.setItemMeta(meta);
        }
        return this;
    }

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
    @Deprecated
    public ItemBuilder setDisplayName(String text){
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(text);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the displayname of the item.
     * @param text The name to change it to.
     */
    public ItemBuilder setDisplayName(Component text){
        ItemMeta im = is.getItemMeta();
        im.displayName(text);
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
     * @param player The player.
     */
    public ItemBuilder setSkullOwner(Player player) {
        if (is.getItemMeta() instanceof SkullMeta) {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwningPlayer(player);
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
    @Deprecated
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
    @Deprecated
    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    public ItemBuilder lore(String... lore){
        ItemMeta im = is.getItemMeta();
        im.lore(Arrays.stream(lore).map(Component::text).collect(Collectors.toList()));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    public ItemBuilder lore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.lore(lore.stream().map(Component::text).collect(Collectors.toList()));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     * @param line The line to remove.
     */
    @Deprecated
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
    @Deprecated
    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());

        if(index<0||index>lore.size()) return this;

        lore.remove(index);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }
    
    /** 
     * @param line The line to remove
     * @return ItemBuilder
     */
    public ItemBuilder removeloreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<Component> lore = new ArrayList<>(im.lore());
        lore.removeIf(component -> component instanceof TextComponent && ((TextComponent) component).content().equals(line));
        im.lore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     * @param index The index of the lore line to remove.
     */
    public ItemBuilder removeloreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<Component> lore = new ArrayList<>(im.lore());
        if (index >= 0 && index < lore.size()) {
            lore.remove(index);
        }
        im.lore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     */
    @Deprecated
    public ItemBuilder addLoreLine(String line){
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(im.hasLore())lore = new ArrayList<>(im.getLore());
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
    @Deprecated
    public ItemBuilder addLoreLine(String line, int pos){
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     */
    public ItemBuilder addLoreLineC(String line){
        ItemMeta im = is.getItemMeta();

        List<Component> lore = new ArrayList<>();
        if (im.hasLore()) {
            lore = new ArrayList<>(im.lore());
        }
        lore.add(Component.text(line));

        im.lore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     * @param pos The index of where to put it.
     */
    public ItemBuilder addLoreLineC(String line, int pos){
        ItemMeta im = is.getItemMeta();
        List<Component> lore = new ArrayList<>(im.lore());
        lore.set(pos, Component.text(line));
        im.lore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     * @param itemFlag The item flags to add
     */
    public ItemBuilder addItemFlags(ItemFlag itemFlag) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(itemFlag);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addAllItemFlags() {
        ItemMeta im = is.getItemMeta();
        for (ItemFlag itemFlag : ItemFlag.values()) {
            im.addItemFlags(itemFlag);
        }
        is.setItemMeta(im);
        return this;
    }

    /**
     * @param data The item data to set
     */
    public ItemBuilder setCustomModelData(int data) {
        ItemMeta im = is.getItemMeta();
        im.setCustomModelData(data);
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
     * Retrieves the itemstack from the ItemBuilder.
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    public ItemStack toItemStack(){
        return is;
    }
}