package fr.cel.gameapi.manager.npc;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class NPCManager implements Listener {

    @Getter protected final Map<String, NPC> npcs = new ConcurrentHashMap<>();

    private final Map<JavaPlugin, NPCManager> npcsPlugin = new HashMap<>();

    private final JavaPlugin main;

    /**
     * Constructor of the NPCManager.
     * @param main Main instance of the plugin.
     */
    public NPCManager(@NotNull JavaPlugin main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
        this.addNPCPlugin(main, this);
    }

    /**
     * Load NPCs.
     */
    public void loadNPCs() {
        this.npcs.clear();

        File npcsFolder = new File(this.main.getDataFolder(), "npcs");
        if (!npcsFolder.exists()) npcsFolder.mkdirs();

        if (npcsFolder.isDirectory()) {
            for (File file : Objects.requireNonNull(npcsFolder.listFiles((dir, name) -> name.endsWith(".yml")))) {
                String name = file.getName().replace(".yml", "");

                ConfigNPC config = new ConfigNPC(main, name);

                NPC npc = config.getNPC();
                if (npc == null) {
                    Bukkit.getConsoleSender().sendMessage(Component.empty().append(Component.text("[" + main.getName() + "] ")).append(Component.text("Le NPC-" + name + " n'a pas pu être chargé.", NamedTextColor.RED)));
                    continue;
                }

                this.npcs.put(name, npc);
                npc.spawn();
            }
        }

        loadCustomNPCs();
        Bukkit.getConsoleSender().sendMessage(Component.empty().append(Component.text("[" + main.getName() + "] ", NamedTextColor.GOLD)).append(Component.text("Chargement de " + npcs.size() + " NPCs pour le plugin " + main.getName(), NamedTextColor.YELLOW)));
    }

    /**
     * Method to load custom NPCs. Override this method in subclasses to add custom NPC loading logic.
     */
    public void loadCustomNPCs() {}

    /**
     * Removes all NPCs for all players.
     */
    public void removeToAll() {
        for (NPC npc : this.npcs.values()) npc.despawn();
    }

    /**
     * Reloads all NPCs by removing them and loading them again.
     */
    public void reloadNPCs() {
        removeToAll();
        loadNPCs();
    }

    public void addNPCPlugin(JavaPlugin plugin, NPCManager npcManager) {
        this.npcsPlugin.put(plugin, npcManager);
    }

    public Collection<NPCManager> getNPCManagers() {
        return npcsPlugin.values();
    }

    /**
     * Handles player interaction with NPCs.
     * @param event The PlayerInteractEntityEvent event.
     */
    @EventHandler
    public void onPlayerInteract(@NotNull final PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            for (NPC npc : this.npcs.values()) {
                if (npc.getUuid().equals(event.getRightClicked().getUniqueId())) npc.interact(event.getPlayer());
            }
        }
    }

    /**
     * Handles player attack on NPCs.
     * @param event The EntityDamageByEntityEvent event.
     */
    @EventHandler
    public void onPlayerAttack(@NotNull final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            for (NPC npc : this.npcs.values()) {
                if (npc.getUuid().equals(event.getEntity().getUniqueId())) {
                    npc.interact(player);
                    event.setCancelled(true);
                }
            }
        }
    }

}