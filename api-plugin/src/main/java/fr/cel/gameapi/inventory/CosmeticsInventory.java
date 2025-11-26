package fr.cel.gameapi.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import fr.cel.gameapi.manager.cosmetic.CosmeticType;
import fr.cel.gameapi.manager.cosmetic.CosmeticsManager;
import fr.cel.gameapi.manager.cosmetic.PlayerCosmetics;
import fr.cel.gameapi.manager.cosmetic.applicator.HatApplicator;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class CosmeticsInventory extends AbstractInventory {

    private final CosmeticsManager cosmeticsManager = GameAPI.getInstance().getCosmeticsManager();
    private final Player player;
    private CosmeticType currentType;
    private int currentPage;

    private static final int ITEMS_PER_PAGE = 28;

    public CosmeticsInventory(Player player) {
        super(Component.text("Cosmétiques"), Rows.SIX);
        this.player = player;
        this.currentType = CosmeticType.HAT;
        this.currentPage = 0;
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

    @Override
    protected void addItems(@NotNull Inventory inv) {
        // Category buttons
        setItem(2, createCategoryItem(CosmeticType.HAT));
        setItem(4, createCategoryItem(CosmeticType.PARTICLE));
        setItem(6, createCategoryItem(CosmeticType.PET));

        // Separator
        for (int i = 9; i < 18; i++) {
            setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).hideTooltip().toItemStack());
        }

        // Cosmetic items
        List<Cosmetic> cosmetics = getOwnedCosmetics(currentType);
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, cosmetics.size());

        for (int i = startIndex; i < endIndex; i++) {
            setItem(18 + (i - startIndex), createCosmeticItem(cosmetics.get(i)));
        }

        // Fitting room
        if (GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) {
            setItem(45, new ItemBuilder(Material.ARMOR_STAND)
                    .itemName(Component.text("Cabine d'essayage", NamedTextColor.AQUA))
                    .toItemStack());
        }

        // Navigation
        if (currentPage > 0) {
            setItem(48, new ItemBuilder(Material.ARROW)
                .itemName(Component.text("<- Page Précédente", NamedTextColor.YELLOW))
                .toItemStack());
        }

        setItem(49, new ItemBuilder(Material.BARRIER)
            .itemName(Component.text("Quitter", NamedTextColor.RED))
            .toItemStack());

        if (endIndex < cosmetics.size()) {
            setItem(50, new ItemBuilder(Material.ARROW)
                .itemName(Component.text("Page Suivante ->", NamedTextColor.YELLOW))
                .toItemStack());
        }

        PlayerCosmetics playerCosmetics = cosmeticsManager.getPlayerCosmetics(player);
        if (playerCosmetics != null && playerCosmetics.getEquippedCosmetic(currentType) != null) {
            setItem(53, new ItemBuilder(Material.LAVA_BUCKET)
                .itemName(Component.text("Retirer", NamedTextColor.RED))
                .toItemStack());
        }
    }

    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack item) {
        int slot = -1;

        Inventory inv = getInv();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack slotItem = inv.getItem(i);
            if (slotItem != null && slotItem.equals(item)) {
                slot = i;
                break;
            }
        }

        if (slot == -1) return;

        handleClick(slot, item);
    }

    private void handleClick(int slot, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (slot == 2) {
            changeCategory(CosmeticType.HAT);
        } else if (slot == 4) {
            changeCategory(CosmeticType.PARTICLE);
        } else if (slot == 6) {
            changeCategory(CosmeticType.PET);
        } else if (slot == 48 && currentPage > 0) {
            currentPage--;
            refresh();
        } else if (slot == 50) {
            List<Cosmetic> cosmetics = getOwnedCosmetics(currentType);
            if ((currentPage + 1) * ITEMS_PER_PAGE < cosmetics.size()) {
                currentPage++;
                refresh();
            }
        } else if (GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player) && slot == 45) {
            cosmeticsManager.getDressingManager().dressPlayer(player);
        } else if (slot == 49) {
            player.closeInventory();
        } else if (slot == 53) {
            handleUnequip();
        } else if (slot >= 18 && slot <= 45) {
            int index = currentPage * ITEMS_PER_PAGE + (slot - 18);
            List<Cosmetic> cosmetics = getOwnedCosmetics(currentType);
            if (index < cosmetics.size()) {
                handleCosmeticClick(cosmetics.get(index));
            }
        }
    }

    private void refresh() {
        createInventory();
        open(player);
    }

    private void changeCategory(CosmeticType type) {
        if (currentType != type) {
            currentType = type;
            currentPage = 0;
            refresh();
        }
    }

    /**
     * Get only the cosmetics owned by the player for the specified type
     * This prevents spoiling unreleased cosmetics
     */
    // TODO: triage par rareté (commun -> mythique, mythique -> commun) ou ordre alphabétique (A -> Z, Z -> A)
    private List<Cosmetic> getOwnedCosmetics(CosmeticType type) {
        PlayerCosmetics playerCosmetics = cosmeticsManager.getPlayerCosmetics(player);
        if (playerCosmetics == null) return List.of();

        return cosmeticsManager.getCosmeticsByType(type).stream()
            .filter(c -> playerCosmetics.ownsCosmetic(c.getId()))
            .sorted(Comparator.comparingInt(Cosmetic::getRarity))
            .toList();
    }

    private ItemStack createCategoryItem(CosmeticType type) {
        Material material = switch (type) {
            case HAT -> Material.GOLDEN_HELMET;
            case PARTICLE -> Material.BLAZE_POWDER;
            case PET -> Material.BONE;
        };

        ItemBuilder builder = new ItemBuilder(material);
        Component name = Component.text(type.getDisplayName())
            .color(currentType == type ? NamedTextColor.GREEN : NamedTextColor.GRAY)
            .decoration(TextDecoration.BOLD, currentType == type);
        builder.itemName(name);

        if (type == CosmeticType.HAT) {
            builder.hideComponents("equippable", "damage_resistant");
        }

        if (currentType == type) {
            builder.addLoreLine(Component.text("Cosmétique sélectionné", NamedTextColor.GREEN));
        } else {
            builder.addLoreLine(Component.text("Cliquez pour voir", NamedTextColor.GRAY));
        }

        return builder.toItemStack();
    }

    private ItemStack createCosmeticItem(Cosmetic cosmetic) {
        PlayerCosmetics playerCosmetics = cosmeticsManager.getPlayerCosmetics(player);
        boolean equipped = playerCosmetics != null && playerCosmetics.isEquipped(cosmetic.getId());
        NamedTextColor rarityColor = Cosmetic.getRarityColor(cosmetic);

        if (cosmetic.getType() == CosmeticType.HAT) {
            HatApplicator hatApplicator = (HatApplicator) cosmeticsManager.getApplicators().get(CosmeticType.HAT);
            ItemBuilder builder = new ItemBuilder(hatApplicator.createHatItem(cosmetic));

            builder.addLoreLine(Component.empty());
            builder.addLoreLine(Component.text("Rareté : ", NamedTextColor.GRAY)
                    .append(Component.text(Cosmetic.getRarityName(cosmetic), rarityColor)));

            builder.addLoreLine(Component.empty());
            if (equipped) {
                builder.addLoreLine(Component.text("✔ ÉQUIPÉ", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
                builder.setGlow(true);
            } else {
                builder.addLoreLine(Component.text("» Cliquez pour équiper «", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, true));
            }

            return builder.toItemStack();
        }

        ItemBuilder builder = new ItemBuilder(cosmetic.getDisplayMaterial());
        builder.itemName(Component.text(cosmetic.getName()).color(rarityColor).decoration(TextDecoration.BOLD, true));

        if (cosmetic.getDescription() != null && !cosmetic.getDescription().isEmpty()) {
            builder.addLoreLine(Component.text(cosmetic.getDescription(), NamedTextColor.GRAY));
        }

        builder.addLoreLine(Component.empty());
        builder.addLoreLine(Component.text("Rareté : ", NamedTextColor.GRAY)
            .append(Component.text(Cosmetic.getRarityName(cosmetic), rarityColor)));

        builder.addLoreLine(Component.empty());
        if (equipped) {
            builder.addLoreLine(Component.text("✔ ÉQUIPÉ", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
            builder.setGlow(true);
        } else {
            builder.addLoreLine(Component.text("» Cliquez pour équiper «", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, true));
        }

        return builder.toItemStack();
    }

    private void handleCosmeticClick(Cosmetic cosmetic) {
        PlayerCosmetics playerCosmetics = cosmeticsManager.getPlayerCosmetics(player);
        if (playerCosmetics == null) return;

        boolean equipped = playerCosmetics.isEquipped(cosmetic.getId());

        if (equipped) {
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Ce cosmétique est déjà équipé !", NamedTextColor.YELLOW)));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            return;
        }

        // Le cosmétique est possédé, on peut l'équiper
        if (cosmeticsManager.equipCosmetic(player, cosmetic.getId())) {
            player.sendMessage(GameAPI.getPrefix().append(Component.text(cosmetic.getName(), Cosmetic.getRarityColor(cosmetic)))
                    .append(Component.text(" équipé !", NamedTextColor.GREEN)));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
            refresh();
        }
    }

    private void handleUnequip() {
        if (cosmeticsManager.unequipCosmetic(player, currentType)) {
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Cosmétique retiré !", NamedTextColor.GREEN)));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
            refresh();
        }
    }

}