package fr.cel.gameapi.manager.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NPCManager implements Listener {

    @Getter private final Map<String, NPC> npcs = new HashMap<>();

    private final JavaPlugin main;

    /**
     * Constructeur du NPCManager.
     * Initialise le NPCManager avec l'instance principale du plugin.
     *
     * @param main L'instance principale du plugin.
     */
    public NPCManager(JavaPlugin main) {
        this.main = main;
        Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[" + main.getName() + "] &rLe Manager des NPCs a été initialisé pour le plugin " + main.getName() + "&r."));
    }

    /**
     * Charge tous les NPCs depuis le dossier "npcs".
     * Chaque NPC est créé et affiché à tous les joueurs.
     */
    public void loadNPCs() {
        this.npcs.clear();

        Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[" + main.getName() + "] &rChargement des NPCs pour le plugin " + main.getName() + "&r..."));

        File npcsFolder = new File(this.main.getDataFolder(), "npcs");
        if (npcsFolder.exists() || npcsFolder.mkdirs()) {
            if (npcsFolder.isDirectory()) {
                for (File file : npcsFolder.listFiles()) {
                    String name = file.getName().replace(".yml", "");

                    ConfigNPC config = new ConfigNPC(main, name);

                    NPC npc = config.getNPC();
                    if (npc == null) {
                        Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&c[" + main.getName() + "] &rLe NPC-" + name + " n'a pas pu être chargé."));
                        continue;
                    }

                    Bukkit.getConsoleSender().sendMessage(ChatUtility.format("&6[" + main.getName() + "] &rChargement du NPC-" + name));

                    this.npcs.put(name, npc);
                    npc.create();
                    npc.showToAll();
                }
            }
        }

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(GameAPI.getInstance(), PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                int entityId = event.getPacket().getIntegers().read(0);
                EnumWrappers.EntityUseAction action = event.getPacket().getEnumEntityUseActions().read(0).getAction();

                if (action == EnumWrappers.EntityUseAction.ATTACK) {
                    npcs.values().stream()
                            .filter(npc -> npc.getNpc().getId() == entityId)
                            .findFirst()
                            .ifPresent(npc -> Bukkit.getScheduler().runTask(main, () -> npc.interact(event.getPlayer())));
                }

                if (action == EnumWrappers.EntityUseAction.INTERACT) {
                    EnumWrappers.Hand hand = event.getPacket().getEnumEntityUseActions().read(0).getHand();
                    if (hand == EnumWrappers.Hand.MAIN_HAND) {
                        npcs.values().stream()
                                .filter(npc -> npc.getNpc().getId() == entityId)
                                .findFirst()
                                .ifPresent(npc -> Bukkit.getScheduler().runTask(main, () -> npc.interact(event.getPlayer())));
                    }
                }
            }
        });
    }

    /**
     * Retire tous les NPCs de tous les joueurs.
     * Cette méthode parcourt la liste des NPCs et appelle removeToAll sur chacun.
     */
    public void removeToAll() {
        for (NPC npc : this.npcs.values()) npc.removeToAll();
    }

    /**
     * Réinitialise le NPCManager en rechargeant tous les NPCs.
     */
    public void reloadNPCs() {
        removeToAll();
        loadNPCs();
    }

    /**
     * Gère l'événement de connexion d'un joueur pour afficher les NPCs.
     * Cette méthode est appelée à chaque fois qu'un joueur rejoint le serveur
     * et affiche tous les NPCs à ce joueur.
     *
     * @param event L'événement PlayerJoinEvent déclenché lors de la connexion du joueur.
     */
    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        this.getNpcs().values().forEach(npc -> npc.spawn(event.getPlayer()));
    }


    /**
     * Gère l'événement de déplacement d'un joueur pour faire regarder les NPCs vers le joueur.
     * Cette méthode est appelée à chaque déplacement d'un joueur et met à jour la direction
     * de tous les NPCs pour qu'ils regardent le joueur.
     *
     * @param event L'événement PlayerMoveEvent déclenché par le déplacement du joueur.
     */
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        Location loc = event.getTo();
        if (loc == null) return;

        for (NPC npc : this.getNpcs().values()) {
            if (loc.getWorld() != npc.getLocation().getWorld()) continue;

            Location newLoc = loc.clone();
            newLoc.setDirection(newLoc.subtract(npc.getNpc().getBukkitEntity().getLocation()).toVector());
            npc.lookAt(event.getPlayer(), newLoc);
        }
    }

    /**
     * Gère l'événement de changement de monde d'un joueur pour afficher les NPCs.
     * Cette méthode est appelée à chaque fois qu'un joueur change de monde et affiche
     * tous les NPCs à ce joueur.
     *
     * @param event L'événement PlayerChangedWorldEvent déclenché lors du changement de monde.
     */
    @EventHandler
    private void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        for (NPC npc : this.getNpcs().values()) npc.spawn(event.getPlayer());
    }

}