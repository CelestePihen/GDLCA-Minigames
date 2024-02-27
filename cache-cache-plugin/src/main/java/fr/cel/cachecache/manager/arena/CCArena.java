package fr.cel.cachecache.manager.arena;

import java.util.*;

import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.cachecache.manager.GroundItem;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import fr.cel.cachecache.manager.arena.state.ArenaState;
import fr.cel.cachecache.manager.arena.state.game.PlayingArenaState;
import fr.cel.cachecache.manager.arena.state.game.WaitingArenaState;
import fr.cel.cachecache.manager.arena.state.pregame.InitArenaState;
import fr.cel.cachecache.manager.arena.state.pregame.PreGameArenaState;
import fr.cel.cachecache.manager.arena.state.pregame.StartingArenaState;
import fr.cel.cachecache.utils.Config;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;

@Getter
public class CCArena {

    // GameManager / Config
    private final CCGameManager gameManager = CCGameManager.getGameManager();
    @Setter private Config config;

    // Names
    private final String nameArena;
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

    // GroundItems
    @Setter private List<GroundItem> availableGroundItems;
    @Setter private List<Location> locationGroundItems;
    private final List<Item> spawnedGroundItems;

    // Locations
    private final Location spawnLoc;
    private final Location waitingLoc;

    // Team Lists
    private final List<UUID> players;
    private final List<UUID> hiders;
    private final List<UUID> seekers;

    // Scoreboard
    private final JGlobalMethodBasedScoreboard scoreboard;

    // Teams
    private final JScoreboardTeam teamHiders;
    private final JScoreboardTeam teamSeekers;

    // FallDamage
    private final boolean fallDamage;

    // Bunker
    @Getter private final Location leverLocation = new Location(Bukkit.getWorld("world"), 54, 52, -217);
    @Getter private final Location redstoneWireLocation = new Location(Bukkit.getWorld("world"), 56, 52, -217);

    public CCArena(String nameArena, String displayName, HunterMode hunterMode, Location spawnLoc, Location waitingLoc, boolean fallDamage) {
        this.nameArena = nameArena;
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

        this.scoreboard = new JGlobalMethodBasedScoreboard();

        this.teamHiders = scoreboard.createTeam("h" + this.getNameArena(), "", ChatColor.GREEN);
        this.teamSeekers = scoreboard.createTeam("s" + this.getNameArena(), "", ChatColor.DARK_RED);

        this.teamHiders.toBukkitTeam(scoreboard.toBukkitScoreboard()).setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
        this.teamSeekers.toBukkitTeam(scoreboard.toBukkitScoreboard()).setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);

        this.teamHiders.toBukkitTeam(scoreboard.toBukkitScoreboard()).setAllowFriendlyFire(false);
        this.teamSeekers.toBukkitTeam(scoreboard.toBukkitScoreboard()).setAllowFriendlyFire(false);

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
        this.scoreboard.addPlayer(player);

        if (joinMessage) {
            sendMessage(player.getDisplayName() + " a rejoint la partie !");

            int minutes = (bestTimer % 3600) / 60;
            int seconds = bestTimer % 60;

            String timerString = String.format("%02dmin%02ds", minutes, seconds);

            player.sendMessage("Map " + displayName + "\nMeilleur cacheur : " + this.bestPlayer + " avec " + timerString);
        }

        if (teleportSpawn) {
            player.teleport(this.getSpawnLoc());
        }

        if (hunterMode == HunterMode.LoupToucheTouche) {
            wolfTimer.put(player.getUniqueId(), 0);
        }

        player.sendTitle(ChatUtility.format("&6Cache-Cache &r- " + this.hunterMode.getName()), this.getDisplayName(), 10, 70, 20);
        player.getInventory().clear();
        player.setGameMode(gameMode);
        player.setGlowing(false);
    }

    /**
     * Permet de retirer le joueur
     * @param player Le joueur
     */
    public void removePlayer(Player player) {
        if (!isPlayerInArena(player)) return;
        this.scoreboard.removePlayer(player);
        wolfTimer.remove(player.getUniqueId());

        if (this.getSeekers().contains(player.getUniqueId())) {
            this.getSeekers().remove(player.getUniqueId());
            this.getTeamSeekers().removePlayer(player);
            player.setGlowing(false);
        } else {
            this.getHiders().remove(player.getUniqueId());
            this.getTeamHiders().removePlayer(player);
        }

        if (this.getPlayers().size() < 2 || (this.getSeekers().isEmpty() || this.getHiders().isEmpty())) {

            if (arenaState instanceof PreGameArenaState) return;

            else if (arenaState instanceof StartingArenaState startingArenaState) {
                startingArenaState.getStartingArenaTask().cancel();
                sendMessage("Démarrage annulé... Vous avez besoin d'au moins 2 joueurs.");
                setArenaState(new PreGameArenaState(this));
            }

            else if (arenaState instanceof WaitingArenaState waitingArenaState) {
                waitingArenaState.getWaitingArenaTask().cancel();
                sendMessage("Partie annulé... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");
            }

            else if (arenaState instanceof PlayingArenaState playingArenaState) {
                if (playingArenaState.getPlayingArenaTask() != null) playingArenaState.getPlayingArenaTask().cancel();
                if (playingArenaState.getPlayingWolfArenaTask() != null) playingArenaState.getPlayingWolfArenaTask().cancel();
                if (playingArenaState.getPlayingBecomeWolfArenaTask() != null) playingArenaState.getPlayingBecomeWolfArenaTask().cancel();
                if (playingArenaState.getGroundItemsArenaTask() != null) playingArenaState.getGroundItemsArenaTask().cancel();
                sendMessage("Partie annulé... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");
                getWolfTimer().remove(player.getUniqueId());
            }

            this.getPlayers().remove(player.getUniqueId());
            checkWin();
        }
    }

    /**
     * Permet d'éliminer le joueur
     * @param victim Le joueur qui est éliminé
     */
    public void eliminate(Player victim) {

        switch (this.hunterMode) {
            case OneHunter -> {
                victim.sendMessage(gameManager.getPrefix() + "Tu es mort(e). Tu deviens spectateur !");
                victim.sendTitle("Tu es mort(e).", "Tu es spectateur !", 10, 70, 20);
                becomeSpectator(victim);
            }

            case LoupToucheTouche -> {
                victim.sendMessage(gameManager.getPrefix() + "Tu es le loup ! Touches vite un autre joueur sinon vous perdez !");
                victim.sendTitle("Tu es mort(e) !", "Tu deviens le loup !", 10, 70, 20);
                becomeWolf(victim);
            }

            default -> {
                victim.sendMessage(gameManager.getPrefix() + "Tu es mort(e). Tu passes du côté des chercheurs !");
                victim.sendTitle("Tu es mort(e) !", "Tu es devenu(e) chercheur.", 10, 70, 20);
                becomeSeeker(victim);
            }
        }

        if (hunterMode != HunterMode.LoupToucheTouche) {
            checkWin();
        }
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
        teamSeekers.addPlayer(deadPlayer);
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

    public void becomeWolf(Player hitPlayer) {
        // Loup
        Player wolf = Bukkit.getPlayer(seekers.get(0));
        seekers.remove(seekers.get(0));
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
        } else {
            return null;
        }
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
            teamHiders.removePlayer(player);
            teamSeekers.removePlayer(player);
            scoreboard.removePlayer(player);
        });
        hiders.clear();
        seekers.clear();
        players.clear();
        wolfTimer.clear();
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
                sendWinnerMessage();
                timer = 0;

                if (nameArena.equalsIgnoreCase("bunker")) {
                    Block lever = Bukkit.getWorld("world").getBlockAt(leverLocation);
                    if (lever.getBlockData() instanceof Powerable powerable) {
                        powerable.setPowered(true);
                        lever.setBlockData(powerable);
                    }

                    Block redstoneWire = Bukkit.getWorld("world").getBlockAt(redstoneWireLocation);
                    redstoneWire.setType(Material.AIR);
                    redstoneWire.setType(Material.REDSTONE_WIRE);
                }

                setArenaState(new InitArenaState(this));
                players.forEach(pls -> {
                    Player player = Bukkit.getPlayer(pls);
                    if (player == null) return;
                    player.setGlowing(false);
                    gameManager.getPlayerManager().sendPlayerToHub(player);
                    teamHiders.removePlayer(player);
                    teamSeekers.removePlayer(player);
                    scoreboard.removePlayer(player);
                });
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
        player.getInventory().addItem(
                new ItemBuilder(Material.STICK)
                        .setDisplayName("Le tueur de cacheurs")
                        .addEnchant(Enchantment.DAMAGE_ALL, 100)
                        .addLoreLine("Ce bâton a déjà tué de nombreuses personnes.")
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
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
            if (player != null) player.sendMessage(message);
        }
    }

    /**
     * Envoie un son à tous les joueurs dans l'arène
     * @param sound Le son à faire entendre
     */
    public void playSound(Sound sound) {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    /**
     * Permet de changer le niveau d'expérience des joueurs
     * @param level Le nombre de niveau d'expérience
     */
    public void setLevel(int level) {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setLevel(level);
        }
    }

    /**
     * Permet de vider l'inventaire des joueurs
     */
    public void clearPlayers() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.getInventory().clear();
        }
    }

    /**
     * Permet de changer le mode de jeu des joueurs
     * @param gameMode Le mode de jeu
     */
    public void setGameModePlayers(GameMode gameMode) {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setGameMode(gameMode);
        }
    }

    /**
     * Permet de changer le point d'apparition des joueurs
     */
    public void setSpawnPoint() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setBedSpawnLocation(spawnLoc, true);
        }
    }

    /**
     * Permet d'enlever tous les effets de potion des joueurs
     */
    public void clearPotionEffects() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) {
                for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                    player.removePotionEffect(potionEffect.getType());
                }
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

        // Modes de jeu temporaires
        LoupToucheTouche("Loup Touche-Touche"),
        Classique("Mode Classique"),
        TousContreUn("Mode Tous contre Un"),
        PluieDeBonus("Pluie de Bonus"),
        Beta("Beta")
        ;

        private final String name;

        HunterMode(String name) {
            this.name = name;
        }

    }

}