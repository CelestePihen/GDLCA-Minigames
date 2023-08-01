package fr.cel.eldenrpg.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.npc.NPCManager;
import fr.cel.eldenrpg.manager.player.ERPlayer;
import fr.cel.eldenrpg.manager.player.PlayerManager;
import fr.cel.eldenrpg.manager.npc.NPC;
import fr.cel.eldenrpg.utils.RegionsUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerListener extends ERListener {

    private final Set<UUID> playersDead;

    public PlayerListener(EldenRPG main) {
        super(main);
        this.playersDead = new HashSet<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ERPlayer erPlayer = main.getPlayerManager().loadData(player.getUniqueId());

        PlayerManager.getPlayerDataMap().put(player.getUniqueId(), erPlayer);

        for (NPC npc : NPCManager.getNpcs().values()) {
            npc.showToAll();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ERPlayer erPlayer = main.getPlayerManager().getPlayerData(player.getUniqueId());

        if (erPlayer != null) {
            PlayerManager.saveData(erPlayer);
            PlayerManager.getPlayerDataMap().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (block == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (block.getType() == Material.SOUL_CAMPFIRE) {
            player.setBedSpawnLocation(event.getClickedBlock().getLocation().subtract(0, 0, 1), true);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.setSaturation(20);
            sendMessage(player, "Votre point d'apparition a été mis à jour. Vos points de vie ont été restaurés ainsi que votre nourriture.");
            sendTitle(player, Component.text("Grâce évanouie découverte", NamedTextColor.YELLOW), Component.text(""), 2, 5, 2);
            player.playSound(player, "eldenrpg:graceevanouiedecouverte", SoundCategory.MASTER, 0.02F, 1);
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (playersDead.contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        final ERPlayer erPlayer = main.getPlayerManager().getPlayerData(player.getUniqueId());

        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) return;

        if (!erPlayer.isHFirstFirecampActivated() && isInRegion(player, RegionsUtils.FIRST_FIRECAMP.getName())) {
            player.sendMessage(Component.text(RegionsUtils.FIRST_FIRECAMP.getHint()).font(Key.key("eldenrpg:default")));
            erPlayer.setHFirstFirecampActivated(true);
        }

        if (!erPlayer.isHPassThroughBlockActivated() && isInRegion(player, RegionsUtils.PASS_THROUGH_BLOCK.getName())) {
            player.sendMessage(Component.text(RegionsUtils.PASS_THROUGH_BLOCK.getHint()).font(Key.key("eldenrpg:default")));
            erPlayer.setHPassThroughBlockActivated(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getPlayer();

        playersDead.add(player.getUniqueId());
        event.setCancelled(true);

        player.playSound(player, "eldenrpg:death", SoundCategory.MASTER, 0.5F, 1);
        sendTitle(player, Component.text("Vous êtes mort.", NamedTextColor.DARK_RED), Component.text(""), 2, 5, 2);

        Bukkit.getScheduler().runTaskLater(main, () -> player.spigot().respawn(), 9 * 20);
    }

    /**
     * Permet de détecter si un joueur est dans une région WorldGuard ou pas
     * @param player Le joueur à détecter
     * @param regionName Le nom de la région
     * @return Retourne si le joueur est dans la région
     */
    public boolean isInRegion(Player player, String regionName) {
        RegionContainer container = main.getWorldGuard().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        for (ProtectedRegion pr : set) if (pr.getId().equalsIgnoreCase(regionName)) return true;
        return false;
    }

}