package fr.cel.gameapi.manager.cosmetic;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.DressingInventory;
import fr.cel.gameapi.manager.npc.DressingNPC;
import fr.cel.gameapi.manager.npc.NPC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DressingManager {

    private static final Location DRESSING_LOCATION = new Location(Bukkit.getWorlds().getFirst(), 265.5, 59, 88.5, 160.15F, 2F);
    private static final Location NPC_LOCATION = new Location(Bukkit.getWorlds().getFirst(), 264.5, 59, 85.5, 0F, 0F);

    private final Map<UUID, DressingNPC> npcDressedPlayers = new HashMap<>();

    public DressingManager() {
    }

    /**
     * Dress a player into dressing mode
     * @param player The player
     */
    public void dressPlayer(@NotNull Player player) {
        player.closeInventory();

        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, null);
        }

        player.teleport(DRESSING_LOCATION);

        // TODO: add items for rotation animation
//        player.getInventory().addItem();
//        player.getInventory().addItem();

        DressingNPC playerNPC = new DressingNPC(player, NPC_LOCATION);
        playerNPC.spawn();

        playerNPC.hidePlayer();
        playerNPC.showPlayer(player);
        playerNPC.getMannequin().getEquipment().setHelmet(player.getInventory().getHelmet());
        npcDressedPlayers.put(player.getUniqueId(), playerNPC);
        GameAPI.getInstance().getNpcManager().getNpcs().put("dressing_" + player.getUniqueId(), playerNPC);
    }

    /**
     * Undress a player from dressing mode
     * @param player The player
     */
    public void undressPlayer(@NotNull Player player) {
        DressingNPC dressingNPC = npcDressedPlayers.remove(player.getUniqueId());
        if (dressingNPC == null) return;
        dressingNPC.despawn();

        GameAPI.getInstance().getPlayerManager().sendPlayerToHub(player);
    }

    /**
     * Check if a player is currently dressed
     * @param player The player
     * @return true if dressed
     */
    public boolean isPlayerDressed(@NotNull Player player) {
        return npcDressedPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Open the cosmetic menu for a dressed player
     * @param player The player
     */
    public void openCosmeticMenu(@NotNull Player player) {
        DressingNPC npc = npcDressedPlayers.get(player.getUniqueId());
        if (npc != null) new DressingInventory(player, getDressedNPC(player)).open(player);
    }

    /**
     * Get the DressingNPC for a player
     * @param player The player
     * @return The DressingNPC, or null if not dressed
     */
    public DressingNPC getDressedNPC(@NotNull Player player) {
        return npcDressedPlayers.get(player.getUniqueId());
    }

}