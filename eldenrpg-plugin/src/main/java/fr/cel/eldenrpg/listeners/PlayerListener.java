package fr.cel.eldenrpg.listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.npc.NPCManager;
import fr.cel.eldenrpg.manager.player.ERPlayer;
import fr.cel.eldenrpg.manager.player.PlayerManager;
import fr.cel.eldenrpg.manager.npc.NPC;
import fr.cel.eldenrpg.utils.ChatUtility;
import fr.cel.eldenrpg.utils.RegionsUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.Pose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final Set<UUID> playersDead;
    private final EldenRPG main;

    public PlayerListener(EldenRPG main) {
        this.main = main;
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
            player.sendMessage("Votre point d'apparition a été mis à jour. Vos points de vie ont été restaurés ainsi que votre nourriture.");
            player.sendTitle(ChatUtility.format("§eGrâce évanouie découverte"), "", 20, 50, 20);
            player.playSound(player, "eldenrpg:graceevanouiedecouverte", SoundCategory.MASTER, 0.05F, 1);
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (playersDead.contains(player.getUniqueId())) {
            if (from.getYaw() != to.getYaw() && from.getPitch() != to.getPitch()) return;
            event.setTo(event.getFrom());
            return;
        }

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()
                && from.getYaw() == to.getYaw() && from.getPitch() == to.getPitch()) return;

        final ERPlayer erPlayer = main.getPlayerManager().getPlayerData(player.getUniqueId());

        if (erPlayer == null) {
            player.kickPlayer("Merci de vous reconnecter. Si le problème persiste, contacter un administrateur.");
            return;
        }

        if (!erPlayer.isHFirstFirecampActivated() && isInRegion(player, RegionsUtils.FIRST_FIRECAMP.getName())) {
            TextComponent textComponent = new TextComponent(RegionsUtils.FIRST_FIRECAMP.getHint());
            textComponent.setFont("eldenrpg:default");

            player.sendMessage(textComponent.getText());
            erPlayer.setHFirstFirecampActivated(true);
        }

        if (!erPlayer.isHPassThroughBlockActivated() && isInRegion(player, RegionsUtils.PASS_THROUGH_BLOCK.getName())) {
            TextComponent textComponent = new TextComponent(RegionsUtils.PASS_THROUGH_BLOCK.getHint());
            textComponent.setFont("eldenrpg:default");

            player.sendMessage(textComponent.getText());
            erPlayer.setHPassThroughBlockActivated(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Location deathLocation = player.getLastDeathLocation();

        GameProfile gameProfile = ((CraftPlayer) player).getHandle().getGameProfile();
        Property property = (Property) gameProfile.getProperties().get("textures").toArray()[0];
        String texture = property.getValue();
        String signature = property.getSignature();

        deathLocation.setY(deathLocation.getBlockY());
        while (deathLocation.getBlock().getType() == Material.AIR) {
            deathLocation.subtract(0 , 1 ,0);
        }
        if (deathLocation.getBlock().getType() == Material.GRASS) {
            deathLocation.subtract(0 , 1 ,0);
        }
        if (deathLocation.getBlock().getType() == Material.TALL_GRASS) {
            deathLocation.subtract(0, 1, 0);
        }
        if (deathLocation.getBlock().getType() == Material.DEAD_BUSH) {
            deathLocation.subtract(0, 1, 0);
        }
        deathLocation.setY(deathLocation.getY() + 1);

        NPC npcDead = new NPC("dead" + player.getName(), " ", deathLocation, texture, signature, true, main);
        NPCManager.getNpcs().put(npcDead.getName(), npcDead);

        Bukkit.getScheduler().runTaskLater(main, () -> {
            player.teleport(deathLocation);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1, true, false, true));

            npcDead.create();
            npcDead.showToAll();
            npcDead.getNpc().setPose(Pose.SLEEPING);
        }, 10);
        playersDead.add(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(main, () -> {
            player.playSound(player, "eldenrpg:death", SoundCategory.MASTER, 0.5F, 1);
            player.sendTitle("§4Vous êtes mort", "", 20, 50, 20);

            Bukkit.getScheduler().runTaskLater(main, () -> {
                NPCManager.getNpcs().remove(npcDead.getName());
                npcDead.hideToAll();
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.teleport(player.getBedSpawnLocation());
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                player.setFoodLevel(20);
                player.setSaturation(20);
                playersDead.remove(player.getUniqueId());
            }, 7*20);
        }, 2*20);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Bukkit.getScheduler().runTaskLater(main, () -> event.getEntity().spigot().respawn(), 10);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (playersDead.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (playersDead.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Permet de détecter si un joueur est dans une région WorldGuard ou pas
     * @param player Le joueur à détecter
     * @param regionName Le nom de la région
     * @return Retourne si le joueur est dans la région
     */
    private boolean isInRegion(Player player, String regionName) {
        WorldGuard worldGuard = WorldGuard.getInstance();
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        for (ProtectedRegion pr : set) if (pr.getId().equalsIgnoreCase(regionName)) return true;
        return false;
    }

}