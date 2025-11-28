package fr.cel.hub.manager.event.winter2025;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.database.event.WinterPlayerData;
import fr.cel.gameapi.manager.inventory.InventoryTypes;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.hub.Hub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HeadInventory extends AbstractInventory {

    private static final int HEADS_PER_PAGE = 45; // 5 rows of 9 slots

    private final WinterPlayerData winterPlayerData;
    private final HeadManager headManager;
    private final boolean isSantaInventory;
    private int currentPage = 0;

    public HeadInventory(Player player, boolean isSantaInventory) {
        super(Component.text("Collection de têtes", NamedTextColor.GOLD), Rows.SIX);
        this.type = InventoryTypes.PERSONAL;
        this.winterPlayerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player.getUniqueId()).getWinterPlayerData();
        this.isSantaInventory = isSantaInventory;
        this.headManager = Hub.getInstance().getHeadManager();
    }

    public HeadInventory(Player player, boolean isSantaInventory, int page) {
        this(player, isSantaInventory);
        this.currentPage = page;
    }

    @Override
    protected void addItems(@NotNull Inventory inventory) {
        Set<Integer> allHeadIds = headManager.getAllHeadIds();
        List<Integer> allHeadsList = allHeadIds.stream().sorted().toList();

        List<Integer> foundHeads = winterPlayerData.getHeads();

        // Pagination
        int totalPages = (int) Math.ceil((double) allHeadsList.size() / HEADS_PER_PAGE);
        int startIndex = currentPage * HEADS_PER_PAGE;
        int endIndex = Math.min(startIndex + HEADS_PER_PAGE, allHeadsList.size());

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            int headId = allHeadsList.get(i);
            boolean found = foundHeads.contains(headId);

            ItemBuilder itemBuilder;
            if (found) {
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                profile.setProperty(new ProfileProperty("textures", HeadManager.VALUE_GIFT_TEXTURE));

                itemBuilder = new ItemBuilder(Material.PLAYER_HEAD)
                        .customName(Component.text("Tête #" + headId, NamedTextColor.GREEN))
                        .lore(Component.text("Trouvée", NamedTextColor.GREEN))
                        .setSkullOwner(profile);
            } else {
                itemBuilder = new ItemBuilder(Material.BARRIER)
                        .itemName(Component.text("Tête #" + headId, NamedTextColor.GRAY))
                        .lore(Component.text("Non trouvée", NamedTextColor.RED));
            }

            setItem(slot++, itemBuilder.toItemStack());
        }

        for (int i = 45; i < getSize(); i++) {
            Material decoration = (i % 2 == 0) ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE;
            setItem(i, new ItemBuilder(decoration).hideTooltip().toItemStack());
        }

        if (currentPage > 0) {
            ItemStack prevButton = new ItemBuilder(Material.ARROW)
                    .itemName(Component.text("<- Page précédente", NamedTextColor.YELLOW))
                    .toItemStack();
            setItem(45, prevButton);
        }

        ItemStack statsItem = new ItemBuilder(Material.BOOK)
                .itemName(Component.text("Statistiques", NamedTextColor.GOLD))
                .lore(
                        Component.text("Têtes trouvées : ", NamedTextColor.AQUA)
                                .append(Component.text(foundHeads.size() + "/" + allHeadsList.size(), NamedTextColor.GREEN)),
                        Component.text("Progression : ", NamedTextColor.AQUA)
                                .append(Component.text((foundHeads.size() / allHeadIds.size()) * 100 + "%", NamedTextColor.GREEN)),
                        Component.text("Page : ", NamedTextColor.AQUA)
                                .append(Component.text((currentPage + 1) + "/" + totalPages, NamedTextColor.GREEN))
                )
                .toItemStack();
        setItem(49, statsItem);

        if (isSantaInventory)
            setItem(50, new ItemBuilder(Material.BARRIER)
                    .itemName(Component.text("Quitter", NamedTextColor.RED)).toItemStack());

        if (currentPage < totalPages - 1) {
            ItemStack nextButton = new ItemBuilder(Material.ARROW)
                    .itemName(Component.text("Page suivante ->", NamedTextColor.YELLOW))
                    .toItemStack();
            setItem(53, nextButton);
        }
    }

    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack itemStack) {
        if (itemName.contains("précédente")) {
            new HeadInventory(player, this.isSantaInventory, currentPage - 1).open(player);
        } else if (itemName.contains("suivante")) {
            new HeadInventory(player, this.isSantaInventory, currentPage + 1).open(player);
        }

        if (itemName.equals("Quitter")) {
            new SantaInventory(player).open(player);
        }
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}