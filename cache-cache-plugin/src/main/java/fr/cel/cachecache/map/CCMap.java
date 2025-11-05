package fr.cel.cachecache.map;

import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.map.state.MapState;
import fr.cel.cachecache.map.state.game.PlayingMapState;
import fr.cel.cachecache.map.state.game.WaitingMapState;
import fr.cel.cachecache.map.state.pregame.InitMapState;
import fr.cel.cachecache.map.state.pregame.PreGameMapState;
import fr.cel.cachecache.map.state.pregame.StartingMapState;
import fr.cel.cachecache.utils.CheckAdvancements;
import fr.cel.cachecache.utils.MapConfig;
import fr.cel.gameapi.manager.AdvancementsManager.Advancements;
import fr.cel.gameapi.scoreboard.GameScoreboard;
import fr.cel.gameapi.scoreboard.GameTeam;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Powerable;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.util.*;

public class CCMap {

    @Getter private final GameManager gameManager;
    @Setter private MapConfig mapConfig;

    @Getter private final String mapName;
    @Getter private final String displayName;

    @Getter private MapState mapState;

    @Getter @Setter private int timer = 0;
    @Getter private final Map<UUID, Integer> wolfTimer = new HashMap<>();

    // Best Record
    @Getter private int bestTimer;
    @Getter private String bestPlayer;

    @Getter private String lastHunter;

    @Getter private final CCMode ccMode;

    // Ground Items
    @Getter @Setter private List<GroundItem> availableGroundItems; // Items disponibles pour la map
    @Getter @Setter private List<Location> locationGroundItems; // Position des spawners à Items
    @Getter private final List<UUID> spawnedGroundItems = new ArrayList<>(); // Items qui sont dans la map
    private final List<BukkitRunnable> itemTasks = new ArrayList<>(); // Task

    @Getter private final Location spawnLoc;
    @Getter private final Location waitingLoc;

    // Team Lists
    @Getter private final List<UUID> players = new ArrayList<>();
    @Getter private final List<UUID> hiders = new ArrayList<>();
    @Getter private final List<UUID> seekers = new ArrayList<>();

    private final GameScoreboard scoreboard;

    // Minecraft Teams
    @Getter private final GameTeam teamHiders;
    @Getter private final GameTeam teamSeekers;

    @Getter private final boolean fallDamage;

    // Number of players there were at the start of the game
    @Getter @Setter private int nbPlayerBeginning = 0;

    @Getter private final CheckAdvancements checkAdvancements;

    // Bunker
    @Getter private final Location leverLocation = new Location(Bukkit.getWorld("world"), 54, 52, -217);
    private final YamlConfiguration lampsConfig;

    @Getter private UUID owner = null;

    @Setter private int gameId; // id de la partie dans la BDD

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

        this.teamHiders = scoreboard.registerTeam("hiders", NamedTextColor.GREEN);
        this.teamHiders.setNameTagVisibility(OptionStatus.NEVER);
        this.teamHiders.setAllowFriendlyFire(false);

        this.teamSeekers = scoreboard.registerTeam("seekers", NamedTextColor.RED);
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
            player.sendMessage(Component.text("La partie a déjà été lancée !"));
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

        seekers.remove(player.getUniqueId());
        teamSeekers.removePlayer(player);
        hiders.remove(player.getUniqueId());
        teamHiders.removePlayer(player);

        player.setGlowing(false);

        // s'il y a encore des gens et que le joueur qui vient de quitter était l'hôte alors le joueur ayant rejoint après devient l'hôte
        if (!players.isEmpty() && owner.equals(player.getUniqueId())) {
            Player newOwner = Bukkit.getPlayer(players.getFirst());
            if (newOwner != null) becomeOwner(newOwner);
        }

        if (mapState instanceof PreGameMapState) return;

        if (mapState instanceof StartingMapState startingMapState) {
            startingMapState.getStartingMapTask().cancel();
            sendMessage(Component.text("Démarrage annulé... Un joueur a quitté la partie."));
            setMapState(new PreGameMapState(this));
            setLevel(0);

            Player playerOwner = Bukkit.getPlayer(this.owner);
            if (playerOwner == null || !playerOwner.isOnline()) {
                this.owner = this.players.getFirst();
                playerOwner = Bukkit.getPlayer(this.owner);
            }

            if (playerOwner != null) {
                playerOwner.getInventory().setItem(4, new ItemBuilder(Material.AMETHYST_SHARD).itemName(Component.text("Démarrer la partie")).toItemStack());
            } else {
                gameManager.getMain().getLogger().severe("La carte " + this.getDisplayName() + " n'arrive pas à trouver un gérant.");
            }
            return;
        }

        if (players.size() < 2 || (seekers.isEmpty() || hiders.isEmpty())) {
            Component message = Component.text("Partie annulée... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");

            if (mapState instanceof WaitingMapState) {
                sendMessage(message);
            }

            else if (mapState instanceof PlayingMapState playingMapState) {
                if (playingMapState.getPlayingMapTask() != null) playingMapState.getPlayingMapTask().cancel();
                if (playingMapState.getPlayingWolfMapTask() != null) playingMapState.getPlayingWolfMapTask().cancel();
                if (playingMapState.getPlayingBecomeWolfMapTask() != null) playingMapState.getPlayingBecomeWolfMapTask().cancel();
                if (playingMapState.getGroundItemsMapTask() != null) playingMapState.getGroundItemsMapTask().cancel();

                sendMessage(message);
                getWolfTimer().remove(player.getUniqueId());
            }

            checkWinOrEndGame();
        }
    }

    /**
     * Lance la partie
     * @param sender La personne qui lance la partie
     */
    public void startGame(CommandSender sender) {
        if (getMapState() instanceof PreGameMapState) {
            if (getCcMode() == CCMode.TwoHuntersAtStart) {
                if (getPlayers().size() < CCMode.TwoHuntersAtStart.getRequiredPlayers()) {
                    sender.sendMessage(gameManager.getPrefix().append(Component.text("Il n'y a pas assez de joueurs (minimum 3 joueurs) !")));
                } else {
                    setMapState(new StartingMapState(this));
                }
            }
            else {
                if (getPlayers().size() < 2) {
                    sender.sendMessage(gameManager.getPrefix().append(Component.text("Il n'y a pas assez de joueurs (minimum 2 joueurs) !")));
                } else {
                    setMapState(new StartingMapState(this));
                }
            }
        }
        else {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("La partie est déjà lancée.")));
        }
    }

    /**
     * Permet d'éliminer le joueur
     * @param victim Le joueur qui est éliminé
     */
    public void eliminate(Player victim) {
        Component message = gameManager.getPrefix();
        Component subtitle;

        switch (ccMode) {
            case OneHunter -> {
                message = message.append(Component.text("Tu es mort(e). Tu deviens spectateur !"));
                subtitle = Component.text("Tu es spectateur !");
                becomeSpectator(victim);
                sendMessage(Component.text(victim.getName() + " est mort(e) !"));
            }

            case LoupToucheTouche -> {
                message = message.append(Component.text("Tu es le loup ! Touche vite un autre joueur, sinon tu vas perdre !"));
                subtitle = Component.text("Tu deviens le loup !");
                becomeWolf(victim);
                sendMessage(Component.text(victim.getName() + " est le loup !"));
            }

            default -> {
                message = message.append(Component.text("Tu es mort(e). Tu passes du côté des chercheurs !"));
                subtitle = Component.text("Tu es devenu(e) chercheur.");
                becomeSeeker(victim);
                sendMessage(Component.text(victim.getName() + " est mort(e) !"));
            }
        }

        if (hiders.size() == 1) {
            victim.sendMessage(message);
            victim.showTitle(Title.title(Component.text("Tu es mort(e)."), subtitle, 10, 70, 20));
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
    public OfflinePlayer getPlayerWithLowestTime() {
        UUID uuid = this.players.getFirst();
        int lowestTime = 12000;

        for (Map.Entry<UUID, Integer> entry : wolfTimer.entrySet()) {
            int playerTime = entry.getValue();

            if (playerTime < lowestTime) {
                lowestTime = playerTime;
                uuid = entry.getKey();
            }
        }

        return Bukkit.getOfflinePlayer(uuid);
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

        // Permet d'arrêter toutes les tasks en cours (PointPlayer et Sound)
        itemTasks.forEach(bukkitRunnable -> {
            if (!bukkitRunnable.isCancelled()) bukkitRunnable.cancel();
        });

        checkAdvancements.stopAllChecks();
        gameManager.getStatisticsManager().updateCCGameEnd(this.gameId);

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
     * Donne l'arme "Le tueur de cacheurs" au joueur
     * @param player Le joueur à qui donner l'arme
     */
    public void giveWeapon(Player player) {
        player.getInventory().addItem(new ItemBuilder(Material.STICK)
                        .itemName(Component.text("Le tueur de cacheurs"))
                        .lore(Component.text("Ce bâton a déjà tué de nombreuses personnes..."))
                        .toItemStack());
    }

    /**
     * Active le levier et les lampes de la carte Bunker
     */
    public void activateLeverAndLamps() {
        if (mapName.equalsIgnoreCase("bunker")) {
            Block lever = Bukkit.getWorlds().getFirst().getBlockAt(leverLocation);
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

                Block lampBlock = Bukkit.getWorlds().getFirst().getBlockAt(x, y, z);

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
    public void sendMessage(Component message) {
        message = gameManager.getPrefix().append(message);
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.sendMessage(message);
        }
    }

    /**
     * Envoie un son à tous les joueurs dans l'arène
     * @param sound Le son à faire entendre
     */
    public void playSound(Sound sound) {
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    /**
     * Permet de changer le niveau d'expérience des joueurs
     * @param level Le nombre de niveau d'expérience
     */
    public void setLevel(int level) {
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setLevel(level);
        }
    }

    /**
     * Permet de vider l'inventaire des joueurs
     */
    public void clearPlayers() {
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.getInventory().clear();
        }
    }

    /**
     * Permet de changer le mode de jeu des joueurs
     * @param gameMode Le mode de jeu
     */
    public void setGameModePlayers(GameMode gameMode) {
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setGameMode(gameMode);
        }
    }

    /**
     * Permet de changer le point d'apparition des joueurs
     */
    public void setSpawnPoint() {
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setRespawnLocation(spawnLoc, true);
        }
    }

    /**
     * Permet d'enlever tous les effets de potion des joueurs
     */
    public void clearPotionEffects() {
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) {
                for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                    player.removePotionEffect(potionEffect.getType());
                }
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
        this.mapConfig.setValue("bestPlayer", playerName);
    }

    /**
     * Permet de changer le dernier chercheur de l'arène
     */
    public void setLastHunter(String playerName) {
        this.lastHunter = playerName;
        this.mapConfig.setValue("lastHunter", lastHunter);
    }

    /**
     * Ajoute une task liée à un GroundItem à la liste qui devra être annulée à la fin de la partie
     * @param task La task à ajouter
     */
    public void addItemTask(BukkitRunnable task) {
        this.itemTasks.add(task);
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

        player.showTitle(Title.title(gameManager.getPrefix().append(Component.text(ccMode.getName())),
                Component.text(displayName), 10, 70, 20));

        player.setGameMode(gameMode);
        player.setGlowing(false);

        player.getInventory().clear();
        player.getInventory().setItem(8, new ItemBuilder(Material.BARRIER).itemName(Component.text("Quitter la partie", NamedTextColor.RED)).toItemStack());

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 255, false, false, false));

        // s'il n'y a pas de joueurs dans la carte alors le premier joueur devient l'hôte de la partie
        if (players.isEmpty()) becomeOwner(player);

        players.add(player.getUniqueId());
        scoreboard.addPlayer(player);

        if (joinMessage) sendMessage(player.displayName().append(Component.text(" a rejoint la partie !")));
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
            sendMessage(Component.text("Loup Touche-Touche - Erreur avec le joueur ayant le moins de temps"));

            String name = getPlayerWithLowestTime().getName();
            int time = wolfTimer.get(getPlayerWithLowestTime().getUniqueId());

            if (time < bestTimer) {
                setBestTimer(time);
                setBestPlayer(name);
            }

            String bestTime = String.format("%02dmin%02ds", (time % 3600) / 60, time % 60);

            sendMessage(Component.text("Victoire de " + name + " qui a tenu " + bestTime + " en tant que coureur !"));
        }

        setMapState(new InitMapState(this));
        timer = 0;

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
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
        if (seekers.isEmpty()) sendMessage(Component.text("L'équipe des cacheurs ", NamedTextColor.AQUA)
                .append(Component.text("remporte la partie !", NamedTextColor.WHITE)));

        else if (hiders.isEmpty()) sendMessage(Component.text("L'équipe des chercheurs ", NamedTextColor.RED)
                .append(Component.text("remporte la partie !", NamedTextColor.WHITE)));

        else sendMessage(Component.text("Égalité ?"));
    }

    /**
     * Le joueur devient l'hôte de la partie
     * @param player Le joueur qui va devenir l'hôte de la partie
     */
    private void becomeOwner(Player player) {
        owner = player.getUniqueId();
        player.sendMessage(gameManager.getPrefix().append(Component.text("Tu es désormais l'hôte de la partie !")));
        if (mapState instanceof PreGameMapState) {
            player.getInventory().setItem(4, new ItemBuilder(Material.AMETHYST_SHARD).itemName(Component.text("Démarrer la partie")).toItemStack());
        }
    }

    /**
     * Enlève tous les objets de la carte
     */
    private void clearGroundItems() {
        spawnedGroundItems.forEach(uuid -> {
            Entity entity = Bukkit.getWorlds().getFirst().getEntity(uuid);
            if (entity != null && !entity.isDead()) entity.remove();
        });
        spawnedGroundItems.clear();
    }

}