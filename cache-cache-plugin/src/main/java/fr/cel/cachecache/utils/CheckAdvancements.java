package fr.cel.cachecache.utils;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.map.CCMap;
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

    private final CCMap map;
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

    // T'es pas essouflé ?
    // TODO à implémenter pour la V2
    @Getter private final Set<UUID> playerWhoRun = new HashSet<>();

    public CheckAdvancements(CCMap map) {
        this.map = map;
        this.advancementsManager = map.getGameManager().getAdvancementsManager();

        if (this.map.getMapName().equalsIgnoreCase("chalet")) {
            this.aimezFaireMal = new ZoneMultiplePassage(
                    corner(-2769, 54, -1819),
                    corner(-2766, 51, -1820),
                    this.map.getGameManager().getMain());
        }

        if (this.map.getMapName().equalsIgnoreCase("moulin")) {
            this.miaou = new ZoneDetection(
                    corner(-54, 56, -59),
                    corner(-52, 53, -57),
                    this.map.getGameManager().getMain());
        }

        if (this.map.getMapName().equalsIgnoreCase("bunker")) {
            // côté gauche
            scpPicNic = new ZoneStay(
                    corner(-7, 60, -278),
                    corner(-26, 50, -266),
                    this.map.getGameManager().getMain());

            scpSnow = new ZoneStay(
                    corner(13, 60, -278),
                    corner(-5, 50, -266),
                    this.map.getGameManager().getMain());

            scpWarden = new ZoneStay(
                    corner(32, 60, -278),
                    corner(16, 49, -266),
                    this.map.getGameManager().getMain());

            scpJungle = new ZoneStay(corner(54, 60, -278),
                    corner(35, 48, -266),
                    this.map.getGameManager().getMain());

            // côté droit
            scpAmethyst = new ZoneStay(corner(35, 60, -232),
                    corner(54, 44, -243),
                    this.map.getGameManager().getMain());

            scpRed = new ZoneStay(corner(15, 60, -232),
                    corner(33, 44, -244),
                    this.map.getGameManager().getMain());

            scpBlue = new ZoneStay(corner(-5, 60, -232),
                    corner(13, 49, -244),
                    this.map.getGameManager().getMain());
        }
    }

    /**
     * Active toutes les vérifications des Zones
     */
    public void startAllChecks() {
        if (aimezFaireMal != null) aimezFaireMal.startChecking(map.getPlayers());
        if (miaou != null) miaou.startChecking(map.getPlayers());

        if (scpPicNic != null && scpSnow != null && scpWarden != null && scpJungle != null && scpAmethyst != null && scpRed != null && scpBlue != null) {
            scpPicNic.startChecking(map.getPlayers());
            scpSnow.startChecking(map.getPlayers());
            scpWarden.startChecking(map.getPlayers());
            scpJungle.startChecking(map.getPlayers());
            scpAmethyst.startChecking(map.getPlayers());
            scpRed.startChecking(map.getPlayers());
            scpBlue.startChecking(map.getPlayers());
        }

        // ajoute tous les UUIDs des joueurs dans le dictionnaire
        for (UUID uuid : map.getPlayers()) {
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
        if (player != null) advancementsManager.giveAdvancement(player, Advancements.AUDACIEUX, map.getPlayers());
    }

    /**
     * Donne le succès Collection personnelle aux joueurs ayant tous les objets de la carte Cache-Cache sur laquelle ils sont
     * <br> Donne le succès Rat de laboratoire si un ou plusieurs joueurs sont restés plus de 15 secondes dans toutes les boîtes SCPs sur la carte Bunker
     */
    public void checkCollectionPersonnelleAndRatLabo() {
        for (UUID uuid : map.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (checkItemCollectionPersonnelle(player)) advancementsManager.giveAdvancement(player, Advancements.COLLECTION_PERSONNELLE, map.getPlayers());

            if (map.getMapName().equalsIgnoreCase("bunker") && hasStaySCPs(uuid)) advancementsManager.giveAdvancement(player, Advancements.RAT_LABORATOIRE, map.getPlayers());
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
        for (GroundItem groundItem : map.getAvailableGroundItems()) {
            if (!player.getInventory().contains(groundItem.getItemStack().getType())) return false;
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
            if (player != null && aimezFaireMal.getPlayersPassCount().get(uuid) >= 10)
                advancementsManager.giveAdvancement(player, Advancements.AIMEZ_FAIRE_MAL, map.getPlayers());
        }
    }

    /**
     * Donne le succès Miaou au joueur ayant utilisé fait le son Miaou avec l'objet Sons devant le Chat noir de la carte Moulin
     * @param player L'instance du joueur à qui donner le succès
     */
    public void giveMiaou(Player player) {
        if (player != null) advancementsManager.giveAdvancement(player, Advancements.MIAOU, map.getPlayers());
    }

    /**
     * Donne le succès Monter ou descendre la montagne de sable au joueur ayant été à la plus haute et à la plus basse hauteur en moins de 30 secondes sur la carte Désert
     */
    public void giveMontagneSable(Player player) {
        if (player != null) advancementsManager.giveAdvancement(player, Advancements.MONTAGNE_SABLE, map.getPlayers());
    }

    /**
     * Donne le succès Pas besoin de ça à tous les cacheurs n'ayant pas utilisé d'objets avant 7 minutes
     */
    public void checkPasBesoin() {
        for (UUID uuid : map.getHiders()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && !playersPasBesoin.contains(uuid))
                advancementsManager.giveAdvancement(player, Advancements.PAS_BESOIN, map.getPlayers());
        }
    }

    /**
     * Donne le succès Raid de château au joueur ayant utilisé l'objet Sons avec la corne de chèvre sur la carte Steampunk V1 ou V2
     * @param player L'instance du joueur à qui donner le succès
     */
    public void giveRaidChateau(Player player) {
        if (player != null) advancementsManager.giveAdvancement(player, Advancements.RAID_CHATEAU, map.getPlayers());
    }

    /**
     * Donne le succès Toujours vivant au dernier cacheur restant dans la partie
     */
    public void giveToujoursVivant() {
        Player player = Bukkit.getPlayer(map.getHiders().getFirst());
        if (player != null) advancementsManager.giveAdvancement(player, Advancements.TOUJOURS_VIVANT, map.getPlayers());
    }

    /**
     * Donne le succès Traversée musicale au joueur ayant appuyé sur les 2 boutons de la carte Moulin
     * @param player L'instance du joueur à qui donner le succès
     */
    public void giveTraverseeMusicale(Player player) {
        if (player != null) advancementsManager.giveAdvancement(player, Advancements.TRAVERSEE_MUSICALE, map.getPlayers());
    }

    /**
     * Vérifie et donne le succès Le pied sur le pouvoir aux joueurs ayant marché sur tous les emplacements de GroundItems
     */
    public void checkPiedPouvoir() {
        for (UUID uuid : map.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (piedPouvoir.get(uuid).size() == map.getLocationGroundItems().size())
                advancementsManager.giveAdvancement(player, Advancements.PIED_POUVOIR, map.getPlayers());
        }
    }

    /**
     * Donne le succès L'heure du pique-nique aux joueurs au bout de 20 minutes
     */
    public void givePiqueNique() {
        for (UUID uuid : map.getHiders()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) advancementsManager.giveAdvancement(player, Advancements.PIQUE_NIQUE, map.getPlayers());
        }
    }

    /**
     * Permet de créer une Location rapidement
     *
     * @param x La coordonnée x du bloc
     * @param y La coordonnée y du bloc
     * @param z La coordonnée z du bloc
     * @return Retourne une instance de Location
     * @see org.bukkit.Location
     */
    private Location corner(int x, int y, int z) {
        return new Location(Bukkit.getWorld("world"), x, y, z);
    }

}