package fr.cel.hub.manager.event.winter2025;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import fr.cel.gameapi.manager.cosmetic.CosmeticType;
import fr.cel.gameapi.manager.cosmetic.CosmeticsManager;
import fr.cel.gameapi.manager.cosmetic.PlayerCosmetics;
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

public class ChristmasShopGUI extends AbstractInventory {

    private final CosmeticsManager cosmeticsManager = GameAPI.getInstance().getCosmeticsManager();
    private final Player player;
    private CosmeticType currentType;
    private int currentPage;

    private static final int ITEMS_PER_PAGE = 28;
    private static final String EVENT_TAG = "event_winter2025"; // Tag pour identifier les cosmétiques de Noël

    public ChristmasShopGUI(Player player) {
        super(Component.text("Boutique du Père Noël", NamedTextColor.RED), Rows.SIX);
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
        // Décoration de Noël en haut
        for (int i = 0; i < 9; i++) {
            Material decoration = (i % 2 == 0) ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE;
            setItem(i, new ItemBuilder(decoration).hideTooltip().toItemStack());
        }

        // Category buttons
        setItem(10, createCategoryItem(CosmeticType.HAT));
        setItem(12, createCategoryItem(CosmeticType.PARTICLE));
        setItem(14, createCategoryItem(CosmeticType.PET));
        setItem(16, createEventCurrencyInfoItem());

        // Separator
        for (int i = 18; i < 27; i++) {
            setItem(i, new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).hideTooltip().toItemStack());
        }

        // Cosmetics de Noël disponibles à l'achat
        List<Cosmetic> christmasCosmetics = getWinterCosmetics(currentType);
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, christmasCosmetics.size());

        for (int i = startIndex; i < endIndex; i++) {
            setItem(27 + (i - startIndex), createShopCosmeticItem(christmasCosmetics.get(i)));
        }

        // Décoration en bas
        for (int i = 45; i < 54; i++) {
            Material decoration = (i % 2 == 0) ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE;
            setItem(i, new ItemBuilder(decoration).hideTooltip().toItemStack());
        }

        // Navigation
        if (currentPage > 0) {
            setItem(48, new ItemBuilder(Material.ARROW)
                .itemName(Component.text("<- Page Précédente", NamedTextColor.YELLOW))
                .toItemStack());
        }

        setItem(49, new ItemBuilder(Material.BARRIER)
            .itemName(Component.text("Fermer", NamedTextColor.RED))
            .toItemStack());

        if (endIndex < christmasCosmetics.size()) {
            setItem(50, new ItemBuilder(Material.ARROW)
                .itemName(Component.text("Page Suivante ->", NamedTextColor.YELLOW))
                .toItemStack());
        }
    }

    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack item) {
        int slot = -1;

        // Find the slot of the clicked item
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

        if (slot == 10) {
            changeCategory(CosmeticType.HAT);
        } else if (slot == 12) {
            changeCategory(CosmeticType.PARTICLE);
        } else if (slot == 14) {
            changeCategory(CosmeticType.PET);
        } else if (slot == 48 && currentPage > 0) {
            currentPage--;
            refresh();
        } else if (slot == 50) {
            List<Cosmetic> cosmetics = getWinterCosmetics(currentType);
            if ((currentPage + 1) * ITEMS_PER_PAGE < cosmetics.size()) {
                currentPage++;
                refresh();
            }
        } else if (slot == 49) {
            GameAPI.getInstance().getInventoryManager().openInventory(new SantaInventory(player), player);
        } else if (slot >= 27 && slot <= 44) {
            int index = currentPage * ITEMS_PER_PAGE + (slot - 27);
            List<Cosmetic> cosmetics = getWinterCosmetics(currentType);
            if (index < cosmetics.size()) {
                handlePurchase(cosmetics.get(index));
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

    private List<Cosmetic> getWinterCosmetics(CosmeticType type) {
        return cosmeticsManager.getCosmeticsByType(type).stream()
            .filter(c -> c.getId().startsWith(EVENT_TAG))
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

        if (currentType == type) {
            builder.addLoreLine(Component.text("Catégorie sélectionnée", NamedTextColor.GREEN));
        } else {
            builder.addLoreLine(Component.text("Cliquez pour voir", NamedTextColor.GRAY));
        }

        return builder.toItemStack();
    }

    private ItemStack createShopCosmeticItem(Cosmetic cosmetic) {
        PlayerCosmetics playerCosmetics = cosmeticsManager.getPlayerCosmetics(player);
        boolean owned = playerCosmetics != null && playerCosmetics.ownsCosmetic(cosmetic.getId());

        ItemBuilder builder = new ItemBuilder(Material.PAPER);
        NamedTextColor color = Cosmetic.getRarityColor(cosmetic);
        builder.itemName(Component.text(cosmetic.getName()).color(color).decoration(TextDecoration.BOLD, true));

        if (cosmetic.getDescription() != null && !cosmetic.getDescription().isEmpty()) {
            builder.addLoreLine(Component.text(cosmetic.getDescription(), NamedTextColor.GRAY));
        }

        builder.addLoreLine(Component.empty());
        builder.addLoreLine(Component.text("Rareté: ", NamedTextColor.GRAY)
            .append(Component.text(Cosmetic.getRarityName(cosmetic), color)));

        builder.addLoreLine(Component.empty());

        if (owned) {
            builder.addLoreLine(Component.text("DÉJÀ POSSÉDÉ", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
            builder.setGlow(true);
        } else {
            if (cosmetic.isFree()) {
                builder.addLoreLine(Component.text("Prix: GRATUIT !", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
            } else {
                builder.addLoreLine(Component.text("Prix: ", NamedTextColor.GRAY)
                    .append(Component.text(cosmetic.getPrice() + " ", NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true))
                    .append(Component.text("❄ Flocons de Noël", NamedTextColor.AQUA)));
            }
            builder.addLoreLine(Component.empty());
            builder.addLoreLine(Component.text("» Cliquez pour acheter «", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, true));
        }

        return builder.toItemStack();
    }

    private ItemStack createEventCurrencyInfoItem() {
        int winterPoints = GameAPI.getInstance().getPlayerManager().getPlayerData(player).getWinterPlayerData().getWinterPoints();

        ItemBuilder builder = new ItemBuilder(Material.SNOWBALL);
        builder.itemName(Component.text("Flocons de Noël", NamedTextColor.AQUA).decoration(TextDecoration.BOLD, true));
        builder.addLoreLine(Component.empty());
        builder.addLoreLine(Component.text("Solde: ", NamedTextColor.GRAY)
            .append(Component.text(winterPoints + " flocons de Noël", NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true)));
        builder.addLoreLine(Component.empty());
        builder.addLoreLine(Component.text("Gagnez des flocons de Noël en", NamedTextColor.GRAY));
        builder.addLoreLine(Component.text("participant aux activités", NamedTextColor.GRAY));
        builder.addLoreLine(Component.text("de l'événement de Noël !", NamedTextColor.GRAY));

        return builder.toItemStack();
    }

    private void handlePurchase(Cosmetic cosmetic) {
        PlayerCosmetics playerCosmetics = cosmeticsManager.getPlayerCosmetics(player);
        if (playerCosmetics == null) return;

        // Vérifier si déjà possédé
        if (playerCosmetics.ownsCosmetic(cosmetic.getId())) {
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Vous possédez déjà ce cosmétique !", NamedTextColor.RED)));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            return;
        }

        // Vérifier si gratuit
        if (cosmetic.isFree()) {
            if (cosmeticsManager.unlockCosmetic(player, cosmetic.getId())) {
                player.sendMessage(GameAPI.getPrefix().append(Component.text(cosmetic.getName(), Cosmetic.getRarityColor(cosmetic)))
                    .append(Component.text(" obtenu gratuitement !", NamedTextColor.GREEN)));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.2f);
                refresh();
            }
            return;
        }

        int eventCoins = GameAPI.getInstance().getPlayerManager().getPlayerData(player).getWinterPlayerData().getWinterPoints();
        if (eventCoins < cosmetic.getPrice()) {
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Vous n'avez pas assez de flocons de Noël !", NamedTextColor.RED)));
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Il vous manque ", NamedTextColor.GRAY))
                .append(Component.text((cosmetic.getPrice() - eventCoins) + " flocons de Noël", NamedTextColor.GOLD)));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            return;
        }

        if (GameAPI.getInstance().getPlayerManager().getPlayerData(player).getWinterPlayerData().removeWinterPoints(cosmetic.getPrice())) {
            if (cosmeticsManager.unlockCosmetic(player, cosmetic.getId())) {
                player.sendMessage(GameAPI.getPrefix().append(Component.text(cosmetic.getName(), Cosmetic.getRarityColor(cosmetic)))
                    .append(Component.text(" acheté pour ", NamedTextColor.GREEN))
                    .append(Component.text(cosmetic.getPrice() + " flocons de Noël", NamedTextColor.GOLD))
                    .append(Component.text(" !", NamedTextColor.GREEN)));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 2.0f);
                refresh();
            } else {
                GameAPI.getInstance().getPlayerManager().getPlayerData(player).getWinterPlayerData().addWinterPoints(cosmetic.getPrice());
                player.sendMessage(GameAPI.getPrefix().append(Component.text("Erreur lors de l'achat. Vous avez été remboursé.", NamedTextColor.RED)));
            }
        }
    }

}