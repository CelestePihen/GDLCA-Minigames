package fr.cel.cachecache.utils;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.gameapi.manager.AdvancementsManager;
import fr.cel.gameapi.manager.AdvancementsManager.Advancements;
import fr.cel.gameapi.utils.zone.ZoneDetection;
import fr.cel.gameapi.utils.zone.ZoneMultiplePassage;
import fr.cel.gameapi.utils.zone.ZoneStay;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class CheckAdvancements {

    private final CCArena arena;
    private final AdvancementsManager advancementsManager;

    @Getter private final List<UUID> playersPasBesoin = new ArrayList<>();
    private ZoneMultiplePassage aimezFaireMal;
    @Getter private ZoneDetection miaou;

    // Rat de laboratoire - Map Bunker
    private ZoneStay scpPicNic;
    private ZoneStay scpSnow;
    private ZoneStay scpWarden;
    private ZoneStay scpJungle;
    private ZoneStay scpAmethyst;
    private ZoneStay scpRed;
    private ZoneStay scpBlue;

    @Getter private final Map<UUID, List<Location>> piedPouvoir = new HashMap<>();

    // Succès sans nom
    @Getter private final Set<UUID> playerWhoRun = new HashSet<>();

    public CheckAdvancements(CCArena arena) {
        this.arena = arena;
        this.advancementsManager = arena.getGameManager().getAdvancementsManager();

        if (this.arena.getArenaName().equalsIgnoreCase("chalet")) {
            this.aimezFaireMal = new ZoneMultiplePassage(
                    corner(-2769, 54, -1819),
                    corner(-2766, 51, -1820),
                    this.arena.getGameManager().getMain());
        }

        if (this.arena.getArenaName().equalsIgnoreCase("moulin")) {
            this.miaou = new ZoneDetection(
                    corner(-54, 56, -59),
                    corner(-52, 53, -57),
                    this.arena.getGameManager().getMain());
        }

        if (this.arena.getArenaName().equalsIgnoreCase("bunker")) {
            // côté gauche
            scpPicNic = new ZoneStay(
                    corner(-7, 60, -278),
                    corner(-26, 50, -266),
                    this.arena.getGameManager().getMain());

            scpSnow = new ZoneStay(
                    corner(13, 60, -278),
                    corner(-5, 50, -266),
                    this.arena.getGameManager().getMain());

            scpWarden = new ZoneStay(
                    corner(32, 60, -278),
                    corner(16, 49, -266),
                    this.arena.getGameManager().getMain());

            scpJungle = new ZoneStay(corner(54, 60, -278),
                    corner(35, 48, -266),
                    this.arena.getGameManager().getMain());

            // côté droit
            scpAmethyst = new ZoneStay(corner(35, 60, -232),
                    corner(54, 44, -243),
                    this.arena.getGameManager().getMain());

            scpRed = new ZoneStay(corner(15, 60, -232),
                    corner(33, 44, -244),
                    this.arena.getGameManager().getMain());

            scpBlue = new ZoneStay(corner(-5, 60, -232),
                    corner(13, 49, -244),
                    this.arena.getGameManager().getMain());
        }
    }

    /**
     * Permet de créer une Location rapidement
     * @param x La coordonnée x du bloc
     * @param y La coordonnée y du bloc
     * @param z La coordonnée z du bloc
     * @return Retourne une instance de Location
     * @see org.bukkit.Location
     */
    private Location corner(int x, int y, int z) {
        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

    /**
     * Active toutes les vérifications des Zones
     */
    public void startAllChecks() {
        if (aimezFaireMal != null) aimezFaireMal.startChecking(arena.getPlayers());
        if (miaou != null) miaou.startChecking(arena.getPlayers());

        if (scpPicNic != null && scpSnow != null && scpWarden != null && scpJungle != null && scpAmethyst != null && scpRed != null && scpBlue != null) {
            scpPicNic.startChecking(arena.getPlayers());
            scpSnow.startChecking(arena.getPlayers());
            scpWarden.startChecking(arena.getPlayers());
            scpJungle.startChecking(arena.getPlayers());
            scpAmethyst.startChecking(arena.getPlayers());
            scpRed.startChecking(arena.getPlayers());
            scpBlue.startChecking(arena.getPlayers());
        }

        // ajoute tous les UUIDs des joueurs dans le dictionnaire
        for (UUID uuid : arena.getPlayers()) {
            piedPouvoir.putIfAbsent(uuid, new ArrayList<>());
        }
    }

    /**
     * Vide les listes et arrête toutes les vérifications des Zones
     */
    public void stopAllChecks() {
        playersPasBesoin.clear();
        piedPouvoir.clear();

        if (aimezFaireMal != null) aimezFaireMal.stopChecking();
        if (miaou != null) miaou.stopChecking();

        if (scpPicNic != null && scpSnow != null && scpWarden != null && scpJungle != null && scpAmethyst != null && scpRed != null && scpBlue != null) {
            scpPicNic.stopChecking();
            scpSnow.stopChecking();
            scpWarden.stopChecking();
            scpJungle.stopChecking();
            scpAmethyst.stopChecking();
            scpRed.stopChecking();
            scpBlue.stopChecking();
        }
    }

    /**
     * Donne le succès Audacieux au joueur ayant tapé un chercheur
     * @param player L'instance du joueur à qui donner le succès
     */
    public void giveAudacieux(Player player) {
        if (player == null) return;
        advancementsManager.giveAdvancement(player, Advancements.AUDACIEUX, arena.getPlayers());
    }

    /**
     * Donne le succès Collection personnelle aux joueurs ayant tous les objets de la carte Cache-Cache sur laquelle ils sont
     * <br> Donne le succès Rat de laboratoire si un ou plusieurs joueurs sont restés plus de 15 secondes dans toutes les boîtes SCPs sur la carte Bunker
     */
    public void checkCollectionPersonnelleAndRatLabo() {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (checkItemCollectionPersonnelle(player)) advancementsManager.giveAdvancement(player, Advancements.COLLECTION_PERSONNELLE, arena.getPlayers());

            if (arena.getArenaName().equalsIgnoreCase("bunker") && hasStaySCPs(uuid)) advancementsManager.giveAdvancement(player, Advancements.RAT_LABORATOIRE, arena.getPlayers());
        }
    }

    /**
     * Vérifie si le joueur est resté plus de 15 secondes dans toutes les boîtes SCPs sur la carte Bunker
     * @param uuid L'UUID du joueur
     * @return Vrai s'il est resté plus de 15 secondes dans toutes les boîtes, faux s'il n'est pas resté plus de 15 secondes dans l'une des boîtes
     */
    private boolean hasStaySCPs(UUID uuid) {
        if (scpPicNic.getPlayersInZone().get(uuid) < 15) return false;
        if (scpSnow.getPlayersInZone().get(uuid) < 15) return false;
        if (scpWarden.getPlayersInZone().get(uuid) < 15) return false;
        if (scpJungle.getPlayersInZone().get(uuid) < 15) return false;
        if (scpAmethyst.getPlayersInZone().get(uuid) < 15) return false;
        if (scpRed.getPlayersInZone().get(uuid) < 15) return false;
        return scpBlue.getPlayersInZone().get(uuid) >= 15;
    }

    /**
     * Vérifie si le joueur a tous les objets de la map Cache-Cache sur laquelle il est
     * @param player L'instance du joueur à qui donner le succès
     * @return Vrai si le joueur contient tous les objets sur lui, faux s'il lui en manque un
     */
    private boolean checkItemCollectionPersonnelle(Player player) {
        if (player == null) return false;

        for (GroundItem groundItem : arena.getAvailableGroundItems()) {
            if (!player.getInventory().contains(groundItem.getItemStack().getType())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Donne le succès "Vous aimez vous faire mal" à tous les joueurs qui sont passés 10 fois dans le ralentisseur sur la carte Chalet
     */
    public void checkAimezFaireMal() {
        if (this.aimezFaireMal == null) return;

        for (UUID uuid : aimezFaireMal.getPlayersPassCount().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (aimezFaireMal.getPlayersPassCount().get(uuid) >= 10) advancementsManager.giveAdvancement(player, Advancements.AIMEZ_FAIRE_MAL, arena.getPlayers());
        }
    }

    /**
     * Donne le succès Miaou au joueur ayant utilisé fait le son Miaou avec l'objet Sons devant le Chat noir de la carte Moulin
     * @param player L'instance du joueur à qui donner le succès
     */
    public void giveMiaou(Player player) {
        if (player == null) return;
        advancementsManager.giveAdvancement(player, Advancements.MIAOU, arena.getPlayers());
    }

    /**
     * Donne le succès Monter ou descendre la montagne de sable au joueur ayant été à la plus haute et à la plus basse hauteur en moins de 30 secondes sur la carte Désert
     */
    public void giveMontagneSable(Player player) {
        if (player == null) return;
        advancementsManager.giveAdvancement(player, Advancements.MONTAGNE_SABLE, arena.getPlayers());
    }

    /**
     * Donne le succès Pas besoin de ça à tous les cacheurs n'ayant pas utilisé d'objets avant 7 minutes
     */
    public void checkPasBesoin() {
        for (UUID uuid : arena.getHiders()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (playersPasBesoin.contains(uuid)) continue;
            advancementsManager.giveAdvancement(player, Advancements.PAS_BESOIN, arena.getPlayers());
        }
    }

    /**
     * Donne le succès Raid de château au joueur ayant utilisé l'objet Sons avec la corne de chèvre sur la carte Steampunk V1 ou V2
     * @param player L'instance du joueur à qui donner le succès
     */
    public void giveRaidChateau(Player player) {
        if (player == null) return;
        advancementsManager.giveAdvancement(player, Advancements.RAID_CHATEAU, arena.getPlayers());
    }

    /**
     * Donne le succès Toujours vivant au dernier cacheur restant dans la partie
     */
    public void giveToujoursVivant() {
        Player player = Bukkit.getPlayer(arena.getHiders().getFirst());
        if (player == null) return;
        advancementsManager.giveAdvancement(player, Advancements.TOUJOURS_VIVANT, arena.getPlayers());
    }

    /**
     * Donne le succès Traversée musicale au joueur ayant appuyé sur les 2 boutons de la carte Moulin
     * @param player L'instance du joueur à qui donner le succès
     */
    public void giveTraverseeMusicale(Player player) {
        if (player == null) return;
        advancementsManager.giveAdvancement(player, Advancements.TRAVERSEE_MUSICALE, arena.getPlayers());
    }

    /**
     * Vérifie et donne le succès Le pied sur le pouvoir aux joueurs ayant marché sur tous les emplacements de GroundItems
     */
    public void checkPiedPouvoir() {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (piedPouvoir.get(uuid).size() == arena.getLocationGroundItems().size()) {
                advancementsManager.giveAdvancement(player, Advancements.PIED_POUVOIR, arena.getPlayers());
            }
        }
    }

    /**
     * Donne le succès L'heure du pique-nique aux joueurs au bout de 20 minutes
     */
    public void givePiqueNique() {
        for (UUID uuid : arena.getHiders()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            advancementsManager.giveAdvancement(player, Advancements.PIQUE_NIQUE, arena.getPlayers());
        }
    }

    /**
     * Donne le succès "T'es pas essoufflé" aux cacheurs n'ayant pas couru avant 8 minutes
     */
    public void checkPasEssouffle() {
        for (UUID uuid : arena.getHiders()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            // TODO: advancementsManager.giveAdvancement(player, Advancements.PAS_ESSOUFFLE);
        }
    }

}