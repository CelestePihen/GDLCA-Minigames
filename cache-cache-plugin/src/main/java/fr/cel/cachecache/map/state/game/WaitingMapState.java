package fr.cel.cachecache.map.state.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import fr.cel.cachecache.map.listeners.game.WaitingListenerProvider;
import fr.cel.cachecache.map.state.MapState;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.StatisticsManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class WaitingMapState extends MapState {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 3600;

    public WaitingMapState(CCMap map) {
        super("En partie avant l'arrivée des chercheurs", map);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);

        assignRoles();
        setupHiders();
        displayBestTime();
        updatePlayerStatistics();

        getMap().setMapState(new PlayingMapState(getMap()));
    }

    /**
     * Met à jour les statistiques de tous les joueurs
     */
    private void updatePlayerStatistics() {
        StatisticsManager statsManager = GameAPI.getInstance().getStatisticsManager();

        getMap().getPlayers().forEach(uuidPlayer -> {
            statsManager.updatePlayerStatistic(uuidPlayer, StatisticsManager.PlayerStatistics.CC_GAMES_PLAYED, 1);

            if (getMap().getHiders().contains(uuidPlayer)) {
                statsManager.updatePlayerStatistic(uuidPlayer, StatisticsManager.PlayerStatistics.CC_HIDER_COUNT, 1);
            }

            if (getMap().getSeekers().contains(uuidPlayer)) {
                statsManager.updatePlayerStatistic(uuidPlayer, StatisticsManager.PlayerStatistics.CC_SEEKER_COUNT, 1);
            }
        });
    }

    /**
     * Assigne les rôles selon le mode de jeu
     */
    private void assignRoles() {
        switch (getMap().getCcMode()) {
            case TwoHuntersAtStart -> assignTwoSeekers();
            case TousContreUn -> assignSeekersAllVsOne();
            case LoupToucheTouche -> assignSingleSeekerToucheTouche();
            default -> assignSingleSeeker();
        }
    }

    /**
     * Assigne deux chercheurs en évitant le dernier chasseur
     */
    private void assignTwoSeekers() {
        List<Player> availablePlayers = getEligiblePlayers();

        Collections.shuffle(availablePlayers);

        Player seeker1 = availablePlayers.get(0);
        Player seeker2 = availablePlayers.get(1);

        setupSeeker(seeker1);
        setupSeeker(seeker2);
    }

    /**
     * Assigne tous les joueurs sauf un comme chercheurs pour le mode de jeu "Tous Contre Un"
     */
    private void assignSeekersAllVsOne() {
        List<UUID> playerIds = new ArrayList<>(getMap().getPlayers());
        Collections.shuffle(playerIds);

        // Garder le dernier joueur comme cacheur
        for (int i = 0; i < playerIds.size() - 1; i++) {
            Player seeker = Bukkit.getPlayer(playerIds.get(i));
            if (seeker != null) setupSeeker(seeker);
        }
    }

    /**
     * Assigne un seeker pour le mode de jeu "Loup Touche-Touche"
     */
    private void assignSingleSeekerToucheTouche() {
        Player seeker = selectRandomEligiblePlayer();
        if (seeker == null) return;

        getMap().setLastHunter(seeker.getName());
        getMap().getSeekers().add(seeker.getUniqueId());
        getMap().getTeamSeekers().addPlayer(seeker);

        seeker.getInventory().clear();
        getMap().giveWeapon(seeker);
        seeker.teleport(getMap().getWaitingLoc());
    }

    /**
     * Assigne un seeker classique
     */
    private void assignSingleSeeker() {
        Player seeker = selectRandomEligiblePlayer();
        if (seeker == null) return;

        getMap().setLastHunter(seeker.getName());
        setupSeeker(seeker);
    }

    /**
     * Configure un joueur comme chercheur
     */
    private void setupSeeker(Player seeker) {
        if (seeker == null) return;

        getMap().becomeSeeker(seeker);
        seeker.teleport(getMap().getWaitingLoc());
    }

    /**
     * Sélectionne un joueur aléatoire éligible (qui n'était pas le dernier chasseur)
     */
    private Player selectRandomEligiblePlayer() {
        List<Player> eligiblePlayers = getEligiblePlayers();

        if (eligiblePlayers.isEmpty()) return null;

        return eligiblePlayers.get(ThreadLocalRandom.current().nextInt(eligiblePlayers.size()));
    }

    /**
     * Retourne la liste des joueurs éligibles (qui n'étaient pas le dernier chasseur)
     */
    private List<Player> getEligiblePlayers() {
        String lastHunter = getMap().getLastHunter();

        return getMap().getPlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(player -> !player.getName().equalsIgnoreCase(lastHunter))
                .collect(Collectors.toList());
    }

    /**
     * Configure les cacheurs
     */
    private void setupHiders() {
        List<UUID> seekerIds = getMap().getSeekers();

        getMap().getPlayers().stream()
                .filter(playerId -> !seekerIds.contains(playerId))
                .forEach(this::setupHider);
    }

    /**
     * Configure un joueur comme cacheur
     */
    private void setupHider(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) return;

        getMap().getHiders().add(playerId);
        getMap().getTeamHiders().addPlayer(player);
        player.teleport(getMap().getSpawnLoc());
    }

    /**
     * Affiche le meilleur temps de l'arène
     */
    private void displayBestTime() {
        int bestTimer = getMap().getBestTimer();
        String bestPlayer = getMap().getBestPlayer();

        if (bestTimer <= 0 || bestPlayer == null || bestPlayer.isEmpty()) return;

        String formattedTime = formatTime(bestTimer);
        getMap().sendMessage(String.format("Le meilleur temps est de %s détenu par %s.", formattedTime, bestPlayer));
    }

    /**
     * Formate un temps en secondes vers le format HH:MM:SS
     */
    private String formatTime(int totalSeconds) {
        if (totalSeconds <= 0) return "00h00min00s";

        int hours = totalSeconds / SECONDS_PER_HOUR;
        int minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
        int seconds = totalSeconds % SECONDS_PER_MINUTE;

        return String.format("%02dh%02dmin%02ds", hours, minutes, seconds);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new WaitingListenerProvider(getMap());
    }

}