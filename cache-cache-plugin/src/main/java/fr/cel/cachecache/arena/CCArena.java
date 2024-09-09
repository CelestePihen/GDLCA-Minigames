package fr.cel.cachecache.arena;

import fr.cel.cachecache.arena.state.ArenaState;
import fr.cel.cachecache.arena.state.game.PlayingArenaState;
import fr.cel.cachecache.arena.state.game.WaitingArenaState;
import fr.cel.cachecache.arena.state.pregame.InitArenaState;
import fr.cel.cachecache.arena.state.pregame.PreGameArenaState;
import fr.cel.cachecache.arena.state.pregame.StartingArenaState;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.utils.Config;
import fr.cel.gameapi.scoreboard.GameScoreboard;
import fr.cel.gameapi.scoreboard.GameTeam;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.util.*;

@Getter
public class CCArena {

    // GameManager / Config
    private final GameManager gameManager;
    @Setter private Config config;

    // Names
    private final String arenaName;
    private final String displayName;

    // ArenaState / Timer / WolfTimer
    private ArenaState arenaState;
    @Setter private int timer;
    private final Map<UUID, Integer> wolfTimer;

    // Best Record
    @Setter private int bestTimer;
    private String bestPlayer;

    // Last Hunter
    private String lastHunter;

    // Hunter Mode
    private final HunterMode hunterMode;

    // Ground Items
    @Setter private List<GroundItem> availableGroundItems; // Items disponibles pour la map
    @Setter private List<Location> locationGroundItems; // Position des spawners à Items
    private final List<Item> spawnedGroundItems; // Les Items qui sont dans la map

    // Locations
    private final Location spawnLoc;
    private final Location waitingLoc;

    // Team Lists
    private final List<UUID> players;
    private final List<UUID> hiders;
    private final List<UUID> seekers;

    // Scoreboard
    private final GameScoreboard scoreboard;

    // Teams
    private final GameTeam teamHiders;
    private final GameTeam teamSeekers;

    // Dégâts de chute
    private final boolean fallDamage;

    // Bunker
    @Getter private final Location leverLocation = new Location(Bukkit.getWorld("world"), 54, 52, -217);
    @Getter private final Location redstoneWireLocation = new Location(Bukkit.getWorld("world"), 56, 52, -217);

    private UUID owner = null;

    public CCArena(String arenaName, String displayName, HunterMode hunterMode, Location spawnLoc, Location waitingLoc, boolean fallDamage, GameManager gameManager) {
        this.gameManager = gameManager;

        this.arenaName = arenaName;
        this.displayName = displayName;

        this.spawnLoc = spawnLoc;
        this.waitingLoc = waitingLoc;

        this.players = new ArrayList<>();
        this.hiders = new ArrayList<>();
        this.seekers = new ArrayList<>();

        this.arenaState = new InitArenaState(this);
        this.timer = 0;
        this.wolfTimer = new HashMap<>();

        this.hunterMode = hunterMode;

        this.spawnedGroundItems = new ArrayList<>();

        this.scoreboard = new GameScoreboard("CC-" + arenaName);

        this.teamHiders = scoreboard.registerTeam("hiders", ChatColor.GREEN);
        this.teamHiders.setNameTagVisibility(OptionStatus.NEVER);
        this.teamHiders.setAllowFriendlyFire(false);

        this.teamSeekers = scoreboard.registerTeam("seekers", ChatColor.RED);
        this.teamSeekers.setNameTagVisibility(OptionStatus.NEVER);
        this.teamSeekers.setAllowFriendlyFire(false);

        this.fallDamage = fallDamage;
    }

    /**
     * Permet de changer l'état de l'arène
     * @param arenaState L'état de l'arène
     */
    public void setArenaState(ArenaState arenaState) {
        if (this.arenaState != null) this.arenaState.onDisable();
        this.arenaState = arenaState;
        this.arenaState.onEnable(gameManager.getMain());
    }

    /**
     * Permet de détecter si le joueur est dans l'arène
     * @param player Le joueur à détecter
     */
    public boolean isPlayerInArena(Player player) {
        return players.contains(player.getUniqueId());
    }

    /**
     * Permet d'ajouter le joueur dans l'arène
     * @param player Le joueur à ajouter
     */
    public void addPlayer(Player player, boolean tempHub) {
        if (isPlayerInArena(player)) return;

        if (arenaState instanceof StartingArenaState || arenaState instanceof PlayingArenaState || arenaState instanceof WaitingArenaState) {
            player.sendMessage("La partie a déjà été lancée !");
            join(player, GameMode.SPECTATOR, false, true);
        }

        else if (arenaState instanceof PreGameArenaState) {
            join(player, GameMode.ADVENTURE, true, !tempHub);
        }

        else {
            setArenaState(new PreGameArenaState(this));
            join(player, GameMode.ADVENTURE, true, !tempHub);
        }
    }

    private void join(Player player, GameMode gameMode, boolean joinMessage, boolean teleportSpawn) {
        gameManager.getPlayerManager().removePlayerInHub(player);

        players.add(player.getUniqueId());
        scoreboard.addPlayer(player);

        player.getInventory().clear();

        if (players.isEmpty()) {
            becomeOwner(player);
        }

        if (joinMessage) {
            sendMessage(player.getDisplayName() + " a rejoint la partie !");
        }

        if (teleportSpawn) {
            player.teleport(spawnLoc);
        }

        player.sendTitle(ChatUtility.format("&6Cache-Cache &r- " + hunterMode.getName()), displayName, 10, 70, 20);
        player.setGameMode(gameMode);
        player.setGlowing(false);

        if (hunterMode == HunterMode.LoupToucheTouche) {
            wolfTimer.put(player.getUniqueId(), 0);
        }
    }

    /**
     * Permet de retirer le joueur
     * @param player Le joueur
     */
    public void removePlayer(Player player) {
        if (!isPlayerInArena(player)) return;
        players.remove(player.getUniqueId());
        scoreboard.removePlayer(player);
        wolfTimer.remove(player.getUniqueId());

        if (seekers.contains(player.getUniqueId())) {
            seekers.remove(player.getUniqueId());
            teamSeekers.removePlayer(player);
        } else {
            hiders.remove(player.getUniqueId());
            teamHiders.removePlayer(player);
        }

        player.setGlowing(false);

        // s'il y a encore des gens et que le joueur qui vient de quitter était l'hôte alors le joueur ayant rejoint après devient l'hôte
        if (!players.isEmpty() && owner == player.getUniqueId()) {
            becomeOwner(Objects.requireNonNull(Bukkit.getPlayer(players.getFirst())));
        }

        if (arenaState instanceof PreGameArenaState) return;

        if (arenaState instanceof StartingArenaState startingArenaState) {
            startingArenaState.getStartingArenaTask().cancel();
            sendMessage("Démarrage annulé... Un joueur a quitté la partie.");
            setArenaState(new PreGameArenaState(this));
            Objects.requireNonNull(Bukkit.getPlayer(owner)).getInventory().addItem(new ItemBuilder(Material.AMETHYST_SHARD).setDisplayName("Démarrer la partie").toItemStack());
            return;
        }

        if (players.size() < 2 || (seekers.isEmpty() || hiders.isEmpty())) {
            if (arenaState instanceof WaitingArenaState waitingArenaState) {
                waitingArenaState.getWaitingArenaTask().cancel();
                sendMessage("Partie annulée... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");
            }

            else if (arenaState instanceof PlayingArenaState playingArenaState) {
                if (playingArenaState.getPlayingArenaTask() != null) playingArenaState.getPlayingArenaTask().cancel();
                if (playingArenaState.getPlayingWolfArenaTask() != null) playingArenaState.getPlayingWolfArenaTask().cancel();
                if (playingArenaState.getPlayingBecomeWolfArenaTask() != null) playingArenaState.getPlayingBecomeWolfArenaTask().cancel();
                if (playingArenaState.getGroundItemsArenaTask() != null) playingArenaState.getGroundItemsArenaTask().cancel();

                sendMessage("Partie annulée... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");
                getWolfTimer().remove(player.getUniqueId());
            }

            checkWin();
        }
    }

    /**
     * Permet d'éliminer le joueur
     * @param victim Le joueur qui est éliminé
     */
    public void eliminate(Player victim) {
        String message = gameManager.getPrefix();
        String subtitle;

        switch (hunterMode) {
            case OneHunter -> {
                message += "Tu es mort(e). Tu deviens spectateur !";
                subtitle = "Tu es spectateur !";
                becomeSpectator(victim);
            }

            case LoupToucheTouche -> {
                message += "Tu es le loup ! Touche vite un autre joueur, sinon tu vas perdre !";
                subtitle = "Tu deviens le loup !";
                becomeWolf(victim);
            }

            default -> {
                message += "Tu es mort(e). Tu passes du côté des chercheurs !";
                subtitle = "Tu es devenu(e) chercheur.";
                becomeSeeker(victim);
            }
        }

        victim.sendMessage(message);
        victim.sendTitle("Tu es mort(e).", subtitle, 10, 70, 20);

        if (hunterMode != HunterMode.LoupToucheTouche) {
            checkWin();
        }
    }

    /**
     * Permet de transformer le cacheur en chercheur
     * @param deadPlayer Le cacheur qui vient de mourir
     */
    public void becomeSeeker(Player deadPlayer) {
        becomeNonSeeker(deadPlayer);

        if (getTimer() < 600) {
            deadPlayer.setGlowing(true);
        }

        deadPlayer.getInventory().clear();
        deadPlayer.teleport(spawnLoc);
        giveWeapon(deadPlayer);
    }

    /**
     * Permet de transformer le cacheur en spectateur
     * @param deadPlayer Le cacheur qui vient de mourir
     */
    public void becomeSpectator(Player deadPlayer) {
        becomeNonSeeker(deadPlayer);

        deadPlayer.getInventory().clear();
        deadPlayer.setGameMode(GameMode.SPECTATOR);
    }

    /**
     * Permet de transformer le cacheur en loup
     * @param hitPlayer Le cacheur qui vient de mourir
     */
    public void becomeWolf(Player hitPlayer) {
        // Loup
        Player wolf = Bukkit.getPlayer(seekers.getFirst());
        if (wolf == null) return;

        seekers.remove(seekers.getFirst());
        teamSeekers.removePlayer(wolf);

        hiders.add(wolf.getUniqueId());
        teamHiders.addPlayer(wolf);

        wolf.getInventory().clear();

        // Joueur touché
        becomeNonSeeker(hitPlayer);

        hitPlayer.getInventory().clear();
        hitPlayer.teleport(getWaitingLoc());
        Bukkit.getScheduler().runTaskLater(gameManager.getMain(), () -> hitPlayer.teleport(getSpawnLoc()), 20 * 3);
        giveWeapon(hitPlayer);
    }

    // Permet de savoir le joueur avec le moins de temps en Loup
    public Player getPlayerWithLowestTime() {
        UUID uuid = null;
        int lowestTime = 12000;

        for (Map.Entry<UUID, Integer> entry : wolfTimer.entrySet()) {
            int playerTime = entry.getValue();

            if (playerTime < lowestTime) {
                lowestTime = playerTime;
                uuid = entry.getKey();
            }
        }

        if (uuid != null) {
            return Bukkit.getPlayer(uuid);
        }
        return null;
    }

    // Permet de gérer la fin de partie du mode Loup Touche-Touche
    private void endWolf() {
        String name = getPlayerWithLowestTime().getName();
        int time = wolfTimer.get(getPlayerWithLowestTime().getUniqueId());

        if (getPlayerWithLowestTime() == null) {
            sendMessage("Loup Touche-Touche - Erreur avec le joueur ayant le moins de temps");
        }

        if (arenaState instanceof PlayingArenaState playingArenaState) {
            if (playingArenaState.getPlayingArenaTask() != null) playingArenaState.getPlayingArenaTask().cancel();
            if (playingArenaState.getPlayingWolfArenaTask() != null) playingArenaState.getPlayingWolfArenaTask().cancel();
            if (playingArenaState.getPlayingBecomeWolfArenaTask() != null) playingArenaState.getPlayingBecomeWolfArenaTask().cancel();
        }

        if (time < bestTimer) {
            bestTimer = time;
            config.setValue("bestTime", bestTimer);

            bestPlayer = name;
            config.setValue("bestPlayer", bestPlayer);
        }

        String bestTime = String.format("%02dmin%02ds", (time % 3600) / 60, time % 60);

        sendMessage("Victoire de " + name + " qui a tenu " + bestTime + " en tant que coureur !");
        timer = 0;

        setArenaState(new InitArenaState(this));

        players.forEach(pls -> {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) return;
            gameManager.getPlayerManager().sendPlayerToHub(player);
        });
        scoreboard.resetScoreboard();
        hiders.clear();
        seekers.clear();
        players.clear();
        wolfTimer.clear();
    }

    /**
     * Permet de transformer le cacheur en non cacheur
     * @param deadPlayer Le cacheur qui vient de mourir
     */
    private void becomeNonSeeker(Player deadPlayer) {
        if (hunterMode != HunterMode.LoupToucheTouche && hiders.size() == 1) {
            if (timer > bestTimer) {
                setBestTimer();
                setBestPlayer(deadPlayer.getName());
            }
        }

        hiders.remove(deadPlayer.getUniqueId());
        teamHiders.removePlayer(deadPlayer);

        seekers.add(deadPlayer.getUniqueId());
        teamSeekers.removePlayer(deadPlayer);
    }

    /**
     * Permet de vérifier si la victoire doit être activée
     */
    public void checkWin() {
        if (this.hunterMode == HunterMode.LoupToucheTouche) {
            endWolf();
        } else {
            if (seekers.isEmpty() || hiders.isEmpty()) {
                getSpawnedGroundItems().forEach(Entity::remove);
                getSpawnedGroundItems().clear();
                timer = 0;

                if (arenaName.equalsIgnoreCase("bunker")) {
                    Block lever = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(leverLocation);
                    if (lever.getBlockData() instanceof Powerable powerable) {
                        powerable.setPowered(true);
                        lever.setBlockData(powerable);
                    }

                    Block redstoneWire = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(redstoneWireLocation);
                    redstoneWire.setType(Material.AIR);
                    redstoneWire.setType(Material.REDSTONE_WIRE);
                }

                setArenaState(new InitArenaState(this));
                sendWinnerMessage();

                for (UUID uuid : players) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;
                    gameManager.getPlayerManager().sendPlayerToHub(player);
                }

                scoreboard.resetScoreboard();
                hiders.clear();
                seekers.clear();
                players.clear();
            }
        }
    }

    /**
     * Envoie le message des gagnants
     */
    private void sendWinnerMessage() {
        if (seekers.isEmpty()) sendMessage("&bL'équipe des cacheurs &rremporte la partie !");
        else if (hiders.isEmpty()) sendMessage("&cL'équipe des chercheurs &rremporte la partie !");
        else sendMessage("Égalité !");
    }

    /**
     * Donne le tueur de cacheurs
     * @param player Le joueur
     */
    public void giveWeapon(Player player) {
        player.getInventory().addItem(new ItemBuilder(Material.STICK)
                        .setDisplayName("Le tueur de cacheurs")
                        .addLoreLine("Ce bâton a déjà tué de nombreuses personnes...")
                        .toItemStack());
    }

    /**
     * Envoie un message à tous les joueurs dans l'arène
     * @param message Le message à envoyer
     */
    public void sendMessage(String message) {
        message = ChatUtility.format(gameManager.getPrefix() + message);
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.sendMessage(message);
        }
    }

    /**
     * Envoie un son à tous les joueurs dans l'arène
     * @param sound Le son à faire entendre
     */
    public void playSound(Sound sound) {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    /**
     * Permet de changer le niveau d'expérience des joueurs
     * @param level Le nombre de niveau d'expérience
     */
    public void setLevel(int level) {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.setLevel(level);
        }
    }

    /**
     * Permet de vider l'inventaire des joueurs
     */
    public void clearPlayers() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.getInventory().clear();
        }
    }

    /**
     * Permet de changer le mode de jeu des joueurs
     * @param gameMode Le mode de jeu
     */
    public void setGameModePlayers(GameMode gameMode) {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.setGameMode(gameMode);
        }
    }

    /**
     * Permet de changer le point d'apparition des joueurs
     */
    public void setSpawnPoint() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.setRespawnLocation(spawnLoc, true);
        }
    }

    /**
     * Permet d'enlever tous les effets de potion des joueurs
     */
    public void clearPotionEffects() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    /**
     * Les modes de jeu
     */
    @Getter public enum HunterMode {
        Normal("Mode Normal"),
        OneHunter("Mode Tueur seul"),
        TwoHuntersAtStart("Mode Deux Tueurs"),

        // Temporaires
        LoupToucheTouche("Loup Touche-Touche"),
        Classique("Mode Classique"),
        TousContreUn("Mode Tous contre Un"),
        PluieDeBonus("Pluie de Bonus"),
        Beta("Beta");

        private final String name;
        HunterMode(String name) {
            this.name = name;
        }
    }

    /**
     * Permet de changer le record de l'arène
     */
    public void setBestTimer() {
        this.bestTimer = getTimer();
        config.setValue("bestTime", getTimer());
    }

    /**
     * Permet de changer le meilleur joueur de l'arène
     */
    public void setBestPlayer(String playerName) {
        this.bestPlayer = playerName;
        config.setValue("bestPlayer", playerName);
    }

    /**
     * Permet de changer le dernier chercheur de l'arène
     */
    public void setLastHunter(String playerName) {
        this.lastHunter = playerName;
        config.setValue("lastHunter", lastHunter);
    }

    private void becomeOwner(Player player) {
        owner = player.getUniqueId();
        player.sendMessage(gameManager.getPrefix() + "Tu es désormais l'hôte de la partie !");
        if (arenaState instanceof PreGameArenaState) {
            player.getInventory().setItem(4, new ItemBuilder(Material.AMETHYST_SHARD).setDisplayName("Démarrer la partie").toItemStack());
        }
    }

}