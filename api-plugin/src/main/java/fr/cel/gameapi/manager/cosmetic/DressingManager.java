package fr.cel.gameapi.manager.cosmetic;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.DressingInventory;
import fr.cel.gameapi.manager.npc.NPC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DressingManager {

    private static final Location DRESSING_LOCATION = new Location(Bukkit.getWorlds().getFirst(), 265.5, 59, 88.5, 160.15F, 2F);
    private static final Location NPC_LOCATION = new Location(Bukkit.getWorlds().getFirst(), 264.5, 59, 85.5, 0F, 0F);

    // Map of NPC UUID to Player UUID who dressed them
    private final Map<UUID, DressingNPC> npcDressedPlayers = new HashMap<>();

    public DressingManager() {
    }

    /**
     * Dress a player into dressing mode
     * @param player The player
     */
    public void dressPlayer(Player player) {
        player.closeInventory();

        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, null);
        }

        player.teleport(DRESSING_LOCATION);

        // TODO: rotation animation
//        player.getInventory().addItem();
//        player.getInventory().addItem();

        NPC playerNPC = new NPC(player.getName(), null, NPC_LOCATION);
        playerNPC.spawn();

        playerNPC.hidePlayer();
        playerNPC.showPlayer(player);
        playerNPC.getMannequin().getEquipment().setHelmet(player.getInventory().getHelmet());
        npcDressedPlayers.put(player.getUniqueId(), new DressingNPC(playerNPC));
    }

    /**
     * Undress a player from dressing mode
     * @param player The player
     */
    public void undressPlayer(Player player) {
        DressingNPC dressingNPC = npcDressedPlayers.remove(player.getUniqueId());
        if (dressingNPC == null) return;

        NPC npc = dressingNPC.getNpc();
        if (npc != null) {
            npc.despawn();
            GameAPI.getInstance().getPlayerManager().sendPlayerToHub(player);
        }
    }

    /**
     * Check if a player is currently dressed
     * @param player The player
     * @return true if dressed
     */
    public boolean isPlayerDressed(Player player) {
        return npcDressedPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Open the cosmetic menu for a dressed player
     * @param player The player
     */
    public void openCosmeticMenu(Player player) {
        NPC npc = npcDressedPlayers.get(player.getUniqueId()).getNpc();
        if (npc != null) new DressingInventory(player, getDressedNPC(player)).open(player);
    }

    /**
     * Get the DressingNPC for a player
     * @param player The player
     * @return The DressingNPC, or null if not dressed
     */
    public DressingNPC getDressedNPC(Player player) {
        return npcDressedPlayers.get(player.getUniqueId());
    }

    public static class DressingNPC {
        @Getter private final NPC npc;
        private final Map<CosmeticType, String> equippedCosmetics;

        public DressingNPC(NPC npc) {
            this.npc = npc;
            this.equippedCosmetics = new EnumMap<>(CosmeticType.class);
        }

        /**
         * Get the equipped cosmetic of a specific type
         * @param type The cosmetic type
         * @return The cosmetic ID, or null if none equipped
         */
        @Nullable
        public String getEquippedCosmetic(CosmeticType type) {
            return equippedCosmetics.get(type);
        }

        /**
         * Check if a cosmetic is currently equipped
         * @param cosmeticId The cosmetic ID
         * @return true if equipped
         */
        public boolean isEquipped(String cosmeticId) {
            return equippedCosmetics.containsValue(cosmeticId);
        }

        /**
         * Equip a cosmetic of a specific type
         * @param type The cosmetic type
         * @param cosmeticId The cosmetic ID (null to unequip)
         */
        public void equipCosmetic(CosmeticType type, @Nullable String cosmeticId) {
            if (cosmeticId == null) {
                equippedCosmetics.remove(type);
            } else {
                equippedCosmetics.put(type, cosmeticId);
            }
        }

        /**
         * Unequip a specific cosmetic type
         * @param type The cosmetic type
         */
        public void unequip(CosmeticType type) {
            equippedCosmetics.remove(type);
        }

    }

}
