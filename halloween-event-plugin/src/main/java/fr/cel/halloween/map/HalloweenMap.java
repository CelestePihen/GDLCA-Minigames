package fr.cel.halloween.map;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.scoreboard.GameScoreboard;
import fr.cel.gameapi.scoreboard.GameTeam;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.halloween.manager.GameManager;
import fr.cel.halloween.map.state.MapState;
import fr.cel.halloween.map.state.game.PlayingMapState;
import fr.cel.halloween.map.state.game.WaitingMapState;
import fr.cel.halloween.map.state.pregame.InitMapState;
import fr.cel.halloween.map.state.pregame.PreGameMapState;
import fr.cel.halloween.map.state.pregame.StartingMapState;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import java.util.*;

@Getter
public class HalloweenMap {

    private final String mapName;
    private final String displayName;

    private final Location waitingLoc;
    private final Location spawnLoc;

    private MapState mapState;
    @Setter private int timer = 900;
    private final Map<UUID, Integer> soulsCollected;

    @Setter private List<Location> soulLocations;
    private final List<UUID> soulItems;

    @Setter private List<Location> playerSpawnsLocations;

    @Setter private List<Location> chestLocations;
    private final List<Location> activeChests;

    private String lastTracker;

    private final List<UUID> players;
    private final List<UUID> souls;
    private final List<UUID> tracker;
    private final List<UUID> playerDead;

    private final GameScoreboard scoreboard;
    private final GameTeam teamSouls;
    private final GameTeam teamTracker;

    private final GameManager gameManager;

    private final MapConfig mapConfig;

    public HalloweenMap(String mapName, String displayName, Location waitingLoc, Location spawnLoc, MapConfig mapConfig, GameManager gameManager) {
        this.mapName = mapName;
        this.displayName = displayName;

        this.waitingLoc = waitingLoc;
        this.spawnLoc = spawnLoc;

        this.mapState = new InitMapState(this);

        this.soulsCollected = new HashMap<>();

        this.players = new ArrayList<>();
        this.souls = new ArrayList<>();
        this.tracker = new ArrayList<>();
        this.playerDead = new ArrayList<>();

        this.scoreboard = new GameScoreboard("Halloween");

        this.teamSouls = scoreboard.registerTeam("souls", NamedTextColor.GREEN);
        this.teamSouls.setNameTagVisibility(Team.OptionStatus.NEVER);
        this.teamSouls.setAllowFriendlyFire(false);

        this.teamTracker = scoreboard.registerTeam("tracker", NamedTextColor.RED);
        this.teamTracker.setNameTagVisibility(Team.OptionStatus.NEVER);
        this.teamTracker.setAllowFriendlyFire(false);

        this.mapConfig = mapConfig;
        this.gameManager = gameManager;

        this.chestLocations = mapConfig.getChestLocations();
        this.activeChests = new ArrayList<>();

        this.soulLocations = mapConfig.getSoulLocations();
        this.soulItems = new ArrayList<>();

        this.playerSpawnsLocations = mapConfig.getPlayerSpawnsLocations();
    }

    public void setMapState(MapState mapState) {
        if (this.mapState != null) this.mapState.onDisable();
        this.mapState = mapState;
        this.mapState.onEnable(gameManager.getMain());
    }

    public boolean isPlayerInMap(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        if (isPlayerInMap(player)) return;

        if (mapState instanceof StartingMapState || mapState instanceof WaitingMapState || mapState instanceof PlayingMapState) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("La partie est déjà lancée.")));
            join(player, GameMode.SPECTATOR);
        }

        else if (mapState instanceof PreGameMapState) {
            join(player, GameMode.ADVENTURE);
        }

        else {
            setMapState(new PreGameMapState(this));
            join(player, GameMode.ADVENTURE);
        }
    }

    private void join(Player player, GameMode gameMode) {
        gameManager.getPlayerManager().removePlayerInHub(player);

        players.add(player.getUniqueId());
        soulsCollected.putIfAbsent(player.getUniqueId(), 0);
        scoreboard.addPlayer(player);

        player.teleport(waitingLoc);
        player.getInventory().clear();
        player.setGameMode(gameMode);

        player.showTitle(Title.title(Component.text("Halloween", NamedTextColor.GOLD), Component.text("Event")));

        if (gameMode != GameMode.SPECTATOR) sendMessage(player.displayName().append(Component.text(" a rejoint la partie !")));
    }

    public void removePlayer(Player player) {
        if (!isPlayerInMap(player)) return;

        players.remove(player.getUniqueId());
        scoreboard.removePlayer(player);

        // on le retire de l'équipe dans laquelle il était
        tracker.remove(player.getUniqueId());
        teamTracker.removePlayer(player);
        souls.remove(player.getUniqueId());
        teamSouls.removePlayer(player);
        playerDead.remove(player.getUniqueId());

        if (mapState instanceof PreGameMapState) return;

        if (mapState instanceof StartingMapState startingArenaState) {
            if (startingArenaState.getStartingArenaTask() != null) startingArenaState.getStartingArenaTask().cancel();
            sendMessage(Component.text("Démarrage annulé... Un joueur a quitté la partie."));
            setMapState(new PreGameMapState(this));
            return;
        }

        if (players.size() < 2 || (tracker.isEmpty() || souls.isEmpty())) {
            if (mapState instanceof WaitingMapState) {
                sendMessage(Component.text("Partie annulée... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer."));
            }

            else if (mapState instanceof PlayingMapState playingArenaState) {
                if (playingArenaState.getPlayingMapTask() != null) playingArenaState.getPlayingMapTask().cancel();
                if (playingArenaState.getSoulsMapTask() != null) playingArenaState.getSoulsMapTask().cancel();
                sendMessage(Component.text("Partie annulée... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer."));
            }

            checkWinOrEndGame();
        }
    }

    public void checkWinOrEndGame() {
        if (tracker.isEmpty() || timer == 0) {
            setMapState(new InitMapState(this));
            sendWinnerMessage();

            clearSoulItems();
            soulsCollected.clear();

            scoreboard.resetScoreboard();

            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) GameAPI.getInstance().getPlayerManager().sendPlayerToHub(player);
            }

            souls.clear();
            tracker.clear();
            playerDead.clear();
            players.clear();
            timer = 900;
        }
    }

    public void eliminate(Player victim) {
        becomeSpectator(victim);

        victim.sendMessage(GameManager.getPrefix().append(Component.text("Tu es mort(e). Patiente avant ta résurrection.")));
        victim.showTitle(Title.title(Component.text("Tu es mort(e)."), Component.text("Tu es devenu(e) spectateur(trice)")));

        checkWinOrEndGame();
    }

    public void becomeSeeker(Player deadPlayer) {
        tracker.add(deadPlayer.getUniqueId());
        teamTracker.addPlayer(deadPlayer);

        deadPlayer.getInventory().clear();
        deadPlayer.teleport(spawnLoc);
        giveWeapon(deadPlayer);
    }

    public void becomeSpectator(Player deadPlayer) {
        playerDead.add(deadPlayer.getUniqueId());

//        // Bug fix: Supprimer les nether warts de l'inventaire et remettre à 0 le compteur
//        int netherWartCount = 0;
//        for (ItemStack item : deadPlayer.getInventory().getContents()) {
//            if (item != null && item.getType() == Material.NETHER_WART) {
//                netherWartCount += item.getAmount();
//            }
//        }
//
//        deadPlayer.getInventory().clear();
//
//        // Retirer les âmes collectées (les nether warts)
//        UUID uuid = deadPlayer.getUniqueId();
//        int currentSouls = soulsCollected.getOrDefault(uuid, 0);
//        soulsCollected.put(uuid, Math.max(0, currentSouls - netherWartCount));
//        deadPlayer.setLevel(soulsCollected.get(uuid));

        deadPlayer.getInventory().clear();
        deadPlayer.teleport(waitingLoc);
    }

    public void giveWeapon(Player player) {
        player.getInventory().addItem(new ItemBuilder(Material.STICK).itemName(Component.text("Le tueur d'âmes errantes"))
                .addLoreLine(Component.text("Ce bâton a déjà tué de nombreuses âmes errantes...")).toItemStack());
    }

    public void setLastTracker(String playerName) {
        this.lastTracker = playerName;
        mapConfig.setValue("lastTracker", lastTracker);
    }

    public void sendMessage(Component message) {
        message = GameManager.getPrefix().append(message);
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.sendMessage(message);
        }
    }

    public void playSound(Sound sound) {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    public void setLevel(int level) {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setLevel(level);
        }
    }

    public void clearPlayers() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.getInventory().clear();
        }
    }

    public void setGameModePlayers(GameMode gameMode) {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setGameMode(gameMode);
        }
    }

    public void setSpawnPoint() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setRespawnLocation(spawnLoc, true);
        }
    }

    public void clearPotionEffects() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) {
                for (PotionEffect potionEffect : player.getActivePotionEffects())
                    player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    private UUID getTopPlayer() {
        return soulsCollected.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(this.players.getFirst());
    }

    private void sendWinnerMessage() {
        if (tracker.isEmpty())
            sendMessage(Component.text("Le Traqueur", NamedTextColor.RED).append(Component.text(" a choisi le sort au lieu des bonbons...", NamedTextColor.WHITE)));

        else if (souls.isEmpty())
            sendMessage(Component.text("Le Traqueur", NamedTextColor.RED).append(Component.text(" est satisfait de sa chasse !", NamedTextColor.WHITE)));

        else sendMessage(Component.text("Égalité !"));

        sendMessage(Component.text("L'Âme errante", NamedTextColor.AQUA).append(Component.text(" ayant récoltée le plus de Vestiges d'âmes (" + soulsCollected.get(getTopPlayer()) + ") est " + Bukkit.getOfflinePlayer(getTopPlayer()).getName(), NamedTextColor.WHITE)));
    }

    /**
     * Enlève tous les âmes (objets) de la carte
     */
    private void clearSoulItems() {
        this.soulItems.forEach(uuid -> {
            Entity entity = Bukkit.getWorlds().getFirst().getEntity(uuid);
            if (entity != null && !entity.isDead()) entity.remove();
        });
        soulItems.clear();
    }

}

