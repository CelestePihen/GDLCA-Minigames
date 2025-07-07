package fr.cel.cachecache.map;

import fr.cel.cachecache.map.state.MapState;
import fr.cel.cachecache.map.state.game.PlayingMapState;
import fr.cel.cachecache.map.state.game.WaitingMapState;
import fr.cel.cachecache.map.state.pregame.InitMapState;
import fr.cel.cachecache.map.state.pregame.PreGameMapState;
import fr.cel.cachecache.map.state.pregame.StartingMapState;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.utils.CheckAdvancements;
import fr.cel.cachecache.utils.MapConfig;
import fr.cel.gameapi.manager.AdvancementsManager.Advancements;
import fr.cel.gameapi.scoreboard.GameScoreboard;
import fr.cel.gameapi.scoreboard.GameTeam;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Powerable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.util.*;

@Getter
public class CCMap {

    private final GameManager gameManager;
    @Setter private MapConfig mapConfig;

    private final String mapName;
    private final String displayName;

    private MapState mapState;

    @Setter private int timer = 0;
    private final Map<UUID, Integer> wolfTimer = new HashMap<>();

    // Best Record
    private int bestTimer;
    private String bestPlayer;

    private String lastHunter;

    private final CCMode ccMode;

    // Ground Items
    @Setter private List<GroundItem> availableGroundItems; // Items disponibles pour la map
    @Setter private List<Location> locationGroundItems; // Position des spawners à Items
    private final List<Item> spawnedGroundItems = new ArrayList<>(); // Items qui sont dans la map

    private final Location spawnLoc;
    private final Location waitingLoc;

    // Team Lists
    private final List<UUID> players = new ArrayList<>();
    private final List<UUID> hiders = new ArrayList<>();
    private final List<UUID> seekers = new ArrayList<>();

    private final GameScoreboard scoreboard;

    // Minecraft Teams
    private final GameTeam teamHiders;
    private final GameTeam teamSeekers;

    private final boolean fallDamage;

    // Number of players there were at the start of the game
    @Setter private int nbPlayerBeginning = 0;

    private final CheckAdvancements checkAdvancements;

    // Bunker
    @Getter private final Location leverLocation = new Location(Bukkit.getWorld("world"), 54, 52, -217);
    private final YamlConfiguration lampsConfig;

    private UUID owner = null;

    public CCMap(String mapName, String displayName, CCMode ccMode, Location spawnLoc, Location waitingLoc, boolean fallDamage, GameManager gameManager) {
        this.gameManager = gameManager;
        this.lampsConfig = gameManager.getLampsConfig();

        this.mapName = mapName;
        this.displayName = displayName;

        this.spawnLoc = spawnLoc;
        this.waitingLoc = waitingLoc;

        this.mapState = new InitMapState(this);
        this.ccMode = ccMode;
        this.fallDamage = fallDamage;

        this.scoreboard = new GameScoreboard(mapName);

        this.teamHiders = scoreboard.registerTeam("hiders", ChatColor.GREEN);
        this.teamHiders.setNameTagVisibility(OptionStatus.NEVER);
        this.teamHiders.setAllowFriendlyFire(false);

        this.teamSeekers = scoreboard.registerTeam("seekers", ChatColor.RED);
        this.teamSeekers.setNameTagVisibility(OptionStatus.NEVER);
        this.teamSeekers.setAllowFriendlyFire(false);

        this.checkAdvancements = new CheckAdvancements(this);

        activateLeverAndLamps();
    }

    /**
     * Permet de changer l'état de l'arène
     * @param mapState L'état de l'arène
     */
    public void setMapState(MapState mapState) {
        if (this.mapState != null) this.mapState.onDisable();
        this.mapState = mapState;
        this.mapState.onEnable(gameManager.getMain());
    }

    /**
     * Permet de détecter si le joueur est dans l'arène
     * @param player Le joueur à détecter
     */
    public boolean isPlayerInMap(Player player) {
        return players.contains(player.getUniqueId());
    }

    /**
     * Ajoute un joueur dans l'arène
     * @param player Le joueur à ajouter à l'arène
     * @param tempHub Spécifie si on ajoute le joueur dans le Hub temporaire ou pas
     */
    public void addPlayer(Player player, boolean tempHub) {
        if (isPlayerInMap(player)) return;

        if (mapState instanceof StartingMapState || mapState instanceof PlayingMapState || mapState instanceof WaitingMapState) {
            player.sendMessage("La partie a déjà été lancée !");
            join(player, GameMode.SPECTATOR, false, true);
        }

        else if (mapState instanceof PreGameMapState) {
            join(player, GameMode.ADVENTURE, true, !tempHub);
        }

        else {
            setMapState(new PreGameMapState(this));
            join(player, GameMode.ADVENTURE, true, !tempHub);
        }
    }

    /**
     * Retire un joueur de la partie
     * @param player Le joueur qui quitte
     */
    public void removePlayer(Player player) {
        if (!isPlayerInMap(player)) return;

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
        if (!players.isEmpty() && owner.equals(player.getUniqueId())) {
            Player newOwner = Bukkit.getPlayer(players.getFirst());
            if (newOwner != null) becomeOwner(newOwner);
        }

        if (mapState instanceof PreGameMapState) return;

        if (mapState instanceof StartingMapState startingMapState) {
            startingMapState.getStartingMapTask().cancel();
            sendMessage("Démarrage annulé... Un joueur a quitté la partie.");
            setMapState(new PreGameMapState(this));
            setLevel(0);
            Bukkit.getPlayer(owner).getInventory().setItem(4, new ItemBuilder(Material.AMETHYST_SHARD).setItemName("Démarrer la partie").toItemStack());
            return;
        }

        if (players.size() < 2 || (seekers.isEmpty() || hiders.isEmpty())) {
            if (mapState instanceof WaitingMapState) {
                sendMessage("Partie annulée... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");
            }

            else if (mapState instanceof PlayingMapState playingMapState) {
                if (playingMapState.getPlayingMapTask() != null) playingMapState.getPlayingMapTask().cancel();
                if (playingMapState.getPlayingWolfMapTask() != null) playingMapState.getPlayingWolfMapTask().cancel();
                if (playingMapState.getPlayingBecomeWolfMapTask() != null) playingMapState.getPlayingBecomeWolfMapTask().cancel();
                if (playingMapState.getGroundItemsMapTask() != null) playingMapState.getGroundItemsMapTask().cancel();

                sendMessage("Partie annulée... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");
                getWolfTimer().remove(player.getUniqueId());
            }

            checkWinOrEndGame();
        }
    }

    /**
     * Lance la partie
     * @param player Le joueur qui lance
     */
    public void startGame(Player player) {
        if (getMapState() instanceof PreGameMapState) {
            if (getCcMode() == CCMode.TwoHuntersAtStart) {
                if (getPlayers().size() < CCMode.TwoHuntersAtStart.getRequiredPlayers()) {
                    player.sendMessage(gameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 3 joueurs) !");
                } else {
                    setMapState(new StartingMapState(this));
                }
            }
            else {
                if (getPlayers().size() < 2) {
                    player.sendMessage(gameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 2 joueurs) !");
                } else {
                    setMapState(new StartingMapState(this));
                }
            }
        }
        else {
            player.sendMessage(gameManager.getPrefix() + "La partie est déjà lancée.");
        }
    }

    /**
     * Permet d'éliminer le joueur
     * @param victim Le joueur qui est éliminé
     */
    public void eliminate(Player victim) {
        String message = gameManager.getPrefix();
        String subtitle;

        switch (ccMode) {
            case OneHunter -> {
                message += "Tu es mort(e). Tu deviens spectateur !";
                subtitle = "Tu es spectateur !";
                becomeSpectator(victim);
                sendMessage(victim.getName() + " est mort(e) !");
            }

            case LoupToucheTouche -> {
                message += "Tu es le loup ! Touche vite un autre joueur, sinon tu vas perdre !";
                subtitle = "Tu deviens le loup !";
                becomeWolf(victim);
                sendMessage(victim.getName() + " est le loup !");
            }

            default -> {
                message += "Tu es mort(e). Tu passes du côté des chercheurs !";
                subtitle = "Tu es devenu(e) chercheur.";
                becomeSeeker(victim);
                sendMessage(victim.getName() + " est mort(e) !");
            }
        }

        if (hiders.size() == 1) {
            victim.sendMessage(message);
            victim.sendTitle("Tu es mort(e).", subtitle, 10, 70, 20);
        }

        if (ccMode != CCMode.LoupToucheTouche) checkWinOrEndGame();
    }

    /**
     * Permet de transformer le cacheur en chercheur
     * @param deadPlayer Le cacheur qui vient de mourir
     */
    public void becomeSeeker(Player deadPlayer) {
        becomeNonSeeker(deadPlayer);

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

        hiders.add(seekers.getFirst());
        teamHiders.addPlayer(wolf);

        wolf.getInventory().clear();

        // Joueur touché
        becomeNonSeeker(hitPlayer);

        hitPlayer.getInventory().clear();
        hitPlayer.teleport(getWaitingLoc());
        Bukkit.getScheduler().runTaskLater(gameManager.getMain(), () -> hitPlayer.teleport(getSpawnLoc()), 20 * 3);
        giveWeapon(hitPlayer);
    }

    /**
     * Permet de savoir le joueur avec le moins de temps en Loup <br>
     * Seulement dans le mode Loup Touche-Touche
     * @return Retourne une instance du joueur qui a le moins de temps
     */
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

        if (uuid != null) return Bukkit.getPlayer(uuid);
        return null;
    }

    /**
     * Vérifie si la fin de la partie doit être activée
     */
    public void checkWinOrEndGame() {
        // Mode Loup Touche-Touche
        if (this.ccMode == CCMode.LoupToucheTouche) endWolf();

        // Mode normal
        else if (seekers.isEmpty() || hiders.isEmpty()) endGame();
    }

    private void endGame() {
        // Advancement : Le ménage des nuisibles
        if (timer <= 480 && !seekers.isEmpty()) {
            Player player = Bukkit.getPlayer(seekers.getFirst());
            if (player != null) gameManager.getAdvancementsManager().giveAdvancement(player, Advancements.MENAGE_NUISIBLES);
        }

        checkAdvancements.stopAllChecks();

        clearGroundItems();
        activateLeverAndLamps();

        setMapState(new InitMapState(this));
        sendWinnerMessage();

        scoreboard.resetScoreboard();

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            gameManager.getPlayerManager().sendPlayerToHub(player);
        }

        players.clear();
        hiders.clear();
        seekers.clear();
        timer = 0;
    }

    /**
     * Donne le tueur de cacheurs
     * @param player Le joueur
     */
    public void giveWeapon(Player player) {
        player.getInventory().addItem(new ItemBuilder(Material.STICK)
                        .setItemName("Le tueur de cacheurs")
                        .addLoreLine("Ce bâton a déjà tué de nombreuses personnes...")
                        .toItemStack());
    }

    /**
     * Active le levier et les lampes de la carte Bunker
     */
    public void activateLeverAndLamps() {
        if (mapName.equalsIgnoreCase("bunker")) {
            Block lever = Bukkit.getWorld("world").getBlockAt(leverLocation);
            if (lever.getBlockData() instanceof Powerable powerable) {
                powerable.setPowered(true);
                lever.setBlockData(powerable);
            }

            changeLamps(true);
        }
    }

    /**
     * Change l'état des lampes de la carte Bunker
     * @param activate True pour activer les lumières, false pour désactiver
     */
    public void changeLamps(boolean activate) {
        if (lampsConfig.contains("lamps")) {
            for (String key : Objects.requireNonNull(lampsConfig.getConfigurationSection("lamps")).getKeys(false)) {
                int x = lampsConfig.getInt("lamps." + key + ".x");
                int y = lampsConfig.getInt("lamps." + key + ".y");
                int z = lampsConfig.getInt("lamps." + key + ".z");

                Block lampBlock = Bukkit.getWorld("world").getBlockAt(x, y, z);

                if (lampBlock.getType() != Material.REDSTONE_LAMP) continue;

                if (lampBlock.getBlockData() instanceof Lightable lightable) {
                    lightable.setLit(activate);
                    lampBlock.setBlockData(lightable);
                }
            }
        }
    }

    /**
     * Envoie un message à tous les joueurs dans l'arène
     * @param message Le message à envoyer
     */
    public void sendMessage(String message) {
        message = gameManager.getPrefix() + ChatUtility.format(message);
        for (UUID pls : players) {
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
        for (UUID pls : players) {
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
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.setLevel(level);
        }
    }

    /**
     * Permet de vider l'inventaire des joueurs
     */
    public void clearPlayers() {
        for (UUID pls : players) {
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
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.setGameMode(gameMode);
        }
    }

    /**
     * Permet de changer le point d'apparition des joueurs
     */
    public void setSpawnPoint() {
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.setRespawnLocation(spawnLoc, true);
        }
    }

    /**
     * Permet d'enlever tous les effets de potion des joueurs
     */
    public void clearPotionEffects() {
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    @Getter public enum CCMode {
        Normal("Normal"),
        OneHunter("Tueur seul"),
        TwoHuntersAtStart("Deux Tueurs", 3),

        // Temporaires
        LoupToucheTouche("Loup Touche-Touche"),
        Classique("Classique"),
        TousContreUn("Tous contre Un"),
        PluieDeBonus("Pluie de Bonus"),
        Beta("Beta");

        private final String name;
        private final int requiredPlayers;

        CCMode(String name) {
            this(name, 2);
        }

        CCMode(String name, int requiredPlayers) {
            this.name = name;
            this.requiredPlayers = requiredPlayers;
        }
    }

    /**
     * Permet de changer le record de l'arène avec le temps de la partie.
     */
    public void setBestTimer() {
        setBestTimer(timer);
    }

    /**
     * Permet de changer le record de l'arène
     * @param time Le temps à mettre
     */
    public void setBestTimer(int time) {
        this.bestTimer = time;
        mapConfig.setValue("bestTime", time);
    }

    /**
     * Permet de changer le meilleur joueur de l'arène
     */
    public void setBestPlayer(String playerName) {
        this.bestPlayer = playerName;
        mapConfig.setValue("bestPlayer", playerName);
    }

    /**
     * Permet de changer le dernier chercheur de l'arène
     */
    public void setLastHunter(String playerName) {
        this.lastHunter = playerName;
        mapConfig.setValue("lastHunter", lastHunter);
    }

    /**
     * Permet à un joueur de rejoindre l'arène avec les paramètres définis.
     * @param player Le joueur
     * @param gameMode Le mode de jeu dans lequel il doit être
     * @param joinMessage Spécifie si on envoie le message de join aux autres joueurs
     * @param teleportSpawn Spécifie si on téléporte le joueur au spawn de la carte
     */
    private void join(Player player, GameMode gameMode, boolean joinMessage, boolean teleportSpawn) {
        gameManager.getPlayerManager().removePlayerInHub(player);

        player.sendTitle(ChatUtility.format("&6Cache-Cache &r- " + ccMode.getName()), displayName, 10, 70, 20);
        player.setGameMode(gameMode);
        player.setGlowing(false);
        player.getInventory().clear();

        // s'il n'y a pas de joueurs dans la carte alors le joueur devient le gérant de la partie
        if (players.isEmpty()) becomeOwner(player);

        players.add(player.getUniqueId());
        scoreboard.addPlayer(player);

        if (joinMessage) sendMessage(player.getDisplayName() + " a rejoint la partie !");
        if (teleportSpawn) player.teleport(spawnLoc);

        if (ccMode == CCMode.LoupToucheTouche) wolfTimer.put(player.getUniqueId(), 0);
    }

    /**
     * Permet de transformer le cacheur en non cacheur
     * @param deadPlayer Le cacheur qui vient de mourir
     */
    private void becomeNonSeeker(Player deadPlayer) {
        if (ccMode != CCMode.LoupToucheTouche && hiders.size() == 1) {
            if (timer > bestTimer) {
                setBestTimer();
                setBestPlayer(deadPlayer.getName());
            }
        }

        hiders.remove(deadPlayer.getUniqueId());
        teamHiders.removePlayer(deadPlayer);

        seekers.add(deadPlayer.getUniqueId());
        teamSeekers.addPlayer(deadPlayer);
    }

    /**
     * Gére la fin de la partie du mode Loup Touche-Touche
     */
    private void endWolf() {
        if (mapState instanceof PlayingMapState playingMapState) {
            if (playingMapState.getPlayingMapTask() != null) playingMapState.getPlayingMapTask().cancel();
            if (playingMapState.getPlayingWolfMapTask() != null) playingMapState.getPlayingWolfMapTask().cancel();
            if (playingMapState.getPlayingBecomeWolfMapTask() != null) playingMapState.getPlayingBecomeWolfMapTask().cancel();
        }

        if (getPlayerWithLowestTime() == null) {
            sendMessage("Loup Touche-Touche - Erreur avec le joueur ayant le moins de temps");

            String name = getPlayerWithLowestTime().getName();
            int time = wolfTimer.get(getPlayerWithLowestTime().getUniqueId());

            if (time < bestTimer) {
                setBestTimer(time);
                setBestPlayer(name);
            }

            String bestTime = String.format("%02dmin%02ds", (time % 3600) / 60, time % 60);

            sendMessage("Victoire de " + name + " qui a tenu " + bestTime + " en tant que coureur !");
        }

        setMapState(new InitMapState(this));
        timer = 0;

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            gameManager.getPlayerManager().sendPlayerToHub(player);
        }

        scoreboard.resetScoreboard();
        hiders.clear();
        seekers.clear();
        players.clear();
        wolfTimer.clear();
    }

    /**
     * Envoie le message des gagnants aux joueurs
     */
    private void sendWinnerMessage() {
        if (seekers.isEmpty()) sendMessage("&bL'équipe des cacheurs &rremporte la partie !");
        else if (hiders.isEmpty()) sendMessage("&cL'équipe des chercheurs &rremporte la partie !");
        else sendMessage("Égalité ?");
    }

    /**
     * Fait devenir le joueur le "créateur" de la partie
     * @param player Le joueur qui va devenir "créateur" de la partie
     */
    private void becomeOwner(Player player) {
        owner = player.getUniqueId();
        player.sendMessage(gameManager.getPrefix() + "Tu es désormais l'hôte de la partie !");
        if (mapState instanceof PreGameMapState) {
            player.getInventory().setItem(4, new ItemBuilder(Material.AMETHYST_SHARD).setItemName("Démarrer la partie").toItemStack());
        }
    }

    /**
     * Enlève tous les objets de la carte
     */
    private void clearGroundItems() {
        spawnedGroundItems.stream().filter(Objects::nonNull).forEach(Entity::remove);
        spawnedGroundItems.clear();
    }

}