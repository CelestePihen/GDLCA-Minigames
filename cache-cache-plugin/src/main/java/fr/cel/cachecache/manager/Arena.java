package fr.cel.cachecache.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
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
import fr.cel.hub.utils.ChatUtility;
import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;

public class Arena {
    
    // GameManager / Config
    @Getter private GameManager gameManager = GameManager.getGameManager();
    private Config config;

    // Names
    @Getter private String nameArena;
    @Getter private String displayName;

    // ArenaState / Timer
    @Getter private ArenaState arenaState;
    @Getter @Setter private int timer;

    // Best
    @Getter private int bestTimer;
    @Getter private String bestPlayer;

    // Hunter Mode
    @Getter private HunterMode hunterMode;

    // GroundItems
    @Getter private List<GroundItem> availableGroundItems;
    @Getter private List<String> locationGroundItems;
    @Getter private List<Item> spawnedGroundItems;

    // Locations
    @Getter private Location spawnLoc;
    @Getter private Location waitingLoc;

    // Team Lists
    @Getter private List<UUID> players;
    @Getter private List<UUID> hiders;
    @Getter private List<UUID> seekers;

    // Scoreboard
    private JGlobalMethodBasedScoreboard scoreboard;

    // Teams
    @Getter private JScoreboardTeam teamHiders;
    @Getter private JScoreboardTeam teamSeekers;

    // Constructor
    public Arena(String nameArena, String displayName, Location spawnLoc, Location waitingLoc, int bestTimer, String bestPlayer, HunterMode hunterMode, List<GroundItem> availableGroundItems, List<String> locationGroundItems) {
        this.config = new Config(gameManager.getMain(), nameArena);
        
        // Names
        this.nameArena = nameArena;
        this.displayName = displayName;

        // Locations
        this.spawnLoc = spawnLoc;
        this.waitingLoc = waitingLoc;

        // Team List<>
        this.players = new ArrayList<>();
        this.hiders = new ArrayList<>();
        this.seekers = new ArrayList<>();
        
        // ArenaState / Timer
        this.arenaState = new InitArenaState(this);
        this.timer = 0;

        // Timer
        this.bestTimer = bestTimer;
        this.bestPlayer = bestPlayer;

        // Hunter Mode
        this.hunterMode = hunterMode;

        // GroundItems
        this.availableGroundItems = availableGroundItems;
        this.locationGroundItems = locationGroundItems;
        this.spawnedGroundItems = new ArrayList<>();

        // Scoreboard
        this.scoreboard = new JGlobalMethodBasedScoreboard();

        // Team API
        this.teamHiders = scoreboard.createTeam("h" + this.getNameArena(), "", ChatColor.GREEN);
        this.teamSeekers = scoreboard.createTeam("s" + this.getNameArena(), "", ChatColor.DARK_RED);

        this.teamHiders.toBukkitTeam(scoreboard.toBukkitScoreboard()).setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
        this.teamSeekers.toBukkitTeam(scoreboard.toBukkitScoreboard()).setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);

        this.teamHiders.toBukkitTeam(scoreboard.toBukkitScoreboard()).setAllowFriendlyFire(false);
        this.teamSeekers.toBukkitTeam(scoreboard.toBukkitScoreboard()).setAllowFriendlyFire(false);
        
    }

    /**
     * Détecte si le joueur est dans l'arène
     * @param player Le joueur
     */
    public boolean isPlayerInArena(Player player) {
        return players.contains(player.getUniqueId());
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
    public void setBestPlayer() {
        String playerName = Bukkit.getPlayer(getHiders().get(0)).getName();
        this.bestPlayer = playerName;
        config.setValue("bestPlayer", playerName);
    }

    /**
     * Permet d'ajouter le joueur dans l'arène
     * @param player Le joueur
     */
    public void addPlayer(Player player) {
        if (players.contains(player.getUniqueId())) return;

        if (arenaState instanceof StartingArenaState || arenaState instanceof PlayingArenaState || arenaState instanceof WaitingArenaState) {
            player.sendMessage(GameManager.getPrefix() + "Merci d'attendre la fin de la partie pour aller sur " + this.getDisplayName() + ".");
            return;
        } else {
            setArenaState(new PreGameArenaState(this));
        }

        gameManager.getPlayerManager().removePlayerInHub(player);
        players.add(player.getUniqueId());
        this.scoreboard.addPlayer(player);

        sendMessage(player.getDisplayName() + " a rejoint la partie !");

        player.teleport(this.getSpawnLoc());
        player.sendTitle(ChatUtility.format("&6Cache-Cache &r&f- " + this.hunterMode.getName()), this.getDisplayName(), 10, 70, 20);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setGlowing(false);
    }

    /**
     * Permet de retirer le joueur
     * @param player Le joueur
     */
    public void removePlayer(Player player) {
        if (!this.getPlayers().contains(player.getUniqueId())) return;
        this.getPlayers().remove(player.getUniqueId());
        this.scoreboard.removePlayer(player);

        if (this.getSeekers().contains(player.getUniqueId())) {
            this.getSeekers().remove(player.getUniqueId());
            this.getTeamSeekers().removePlayer(player);
            player.setGlowing(false);
        } else {
            this.getHiders().remove(player.getUniqueId());
            this.getTeamHiders().removePlayer(player);
        }

        if (this.getPlayers().size() < 2 || (this.getSeekers().size() <= 0 || this.getHiders().size() <= 0)) {

            if (arenaState instanceof PreGameArenaState) return;

            else if (arenaState instanceof StartingArenaState) {
                StartingArenaState startingArenaState = (StartingArenaState) arenaState;
                startingArenaState.getArenaStartingTask().cancel();
                sendMessage("Démarrage annulé... Vous avez besoin d'au moins 2 joueurs.");
                setArenaState(new PreGameArenaState(this));
            }
            
            else if (arenaState instanceof WaitingArenaState) {
                WaitingArenaState waitingArenaState = (WaitingArenaState) arenaState;
                waitingArenaState.getWaitingArenaTask().cancel();
                sendMessage("Partie annulé... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");
            }
            
            else if (arenaState instanceof PlayingArenaState) {
                PlayingArenaState playingArenaState = (PlayingArenaState) arenaState;
                playingArenaState.getPlayingArenaTask().cancel();
                playingArenaState.getGroundItemsArenaTask().cancel();
                sendMessage("Partie annulé... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");
            }

            checkWin();
        }
    }

    /**
     * Permet d'éliminer le joueur
     * @param victim Le joueur qui est éliminé
     */
    public void eliminate(Player victim) {

        if (this.hunterMode == HunterMode.OneHunter) {
            victim.sendMessage(GameManager.getPrefix() + "Vous êtes mort. Vous devenez spectateur !");
            victim.sendTitle("Vous êtes mort !", "Vous êtes spectateur.", 10, 70, 20);
            becomeSpectator(victim);
        } else {
            victim.sendMessage(GameManager.getPrefix() + "Vous êtes mort. Vous passez du côté des chercheurs !");
            victim.sendTitle("Vous êtes mort !", "Vous êtes devenu chercheur.", 10, 70, 20);
            becomeSeeker(victim);
        }
        
        checkWin();
    }

    /**
     * Permet de transformer le cacheur en chercheur
     * @param deadPlayer Le cacheur qui vient de mourir
     */
    public void becomeSeeker(Player deadPlayer) {
        if (hiders.size() == 1) {
            if (timer > bestTimer) {
                setBestTimer();
                setBestPlayer();
            }
        }

        hiders.remove(deadPlayer.getUniqueId());
        teamHiders.removePlayer(deadPlayer);
    
        seekers.add(deadPlayer.getUniqueId());
        teamSeekers.addPlayer(deadPlayer);
    
        if (getTimer() < 600) {
            deadPlayer.setGlowing(true);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(gameManager.getMain(), () -> deadPlayer.spigot().respawn(), 10);
        deadPlayer.getInventory().clear();
        
        ItemStack stick = new ItemBuilder(Material.STICK).setDisplayName("L'enculeur de cacheurs").addEnchant(Enchantment.DAMAGE_ALL, 100).addLoreLine("Cet épée a déjà tué de nombreuses personnes.").addItemFlags(ItemFlag.HIDE_ENCHANTS).toItemStack();
        deadPlayer.getInventory().addItem(stick);
    }

    /**
     * Permet de transformer le cacheur en spectateur
     * @param deadPlayer Le cacheur qui vient de mourir
     */
    public void becomeSpectator(Player deadPlayer) {
        if (hiders.size() == 1) {
            if (timer > bestTimer) {
                setBestTimer();
                setBestPlayer();
            }
        }

        hiders.remove(deadPlayer.getUniqueId());
        teamHiders.removePlayer(deadPlayer);
    
        seekers.add(deadPlayer.getUniqueId());
        teamSeekers.addPlayer(deadPlayer);
    
        deadPlayer.getInventory().clear();

        Bukkit.getScheduler().scheduleSyncDelayedTask(gameManager.getMain(), () -> deadPlayer.spigot().respawn(), 10);
        deadPlayer.setGameMode(GameMode.SPECTATOR);
    }

    /**
     * Permet de vérifier si la victoire doit être activée
     */
    private void checkWin() {
        if (seekers.isEmpty() || hiders.isEmpty()) {
            sendWinnerMessage();
            timer = 0;
            setArenaState(new InitArenaState(this));
            hiders.clear();
            seekers.clear();
            players.forEach(pls -> {
                Player player = Bukkit.getPlayer(pls);
                player.setGlowing(false);
                teamHiders.removePlayer(player);
                teamSeekers.removePlayer(player);
                this.scoreboard.removePlayer(player);
            });
            sendPlayersToHub();
            players.clear();
            for (Item item : getSpawnedGroundItems()) {
                item.remove();
            }
            getSpawnedGroundItems().clear();
        }
    }

    /**
     * Envoie le message des gagnants
     */
    private void sendWinnerMessage() {
        if (seekers.isEmpty()) sendMessage("&bL'équipe des cacheurs &r&fremporte la partie !");
        else sendMessage("&cL'équipe des chercheurs &r&fremporte la partie !");
    }

    /**
     * Envoie les joueurs au Hub
     */
    private void sendPlayersToHub() {
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) gameManager.getPlayerManager().sendPlayerToHub(player);
        }
    }

    /**
     * Envoie un message à tous les joueurs dans l'arène
     * @param message Le message à envoyer
     */
    public void sendMessage(String message) {
        message = ChatUtility.format(GameManager.getPrefix() + message);
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
    public enum HunterMode {
        Normal("Mode Normal"),
        OneHunter("Mode Tueur seul"),
        TwoHuntersAtStart("Mode Deux Tueurs")
        ;

        @Getter private String name;

        HunterMode(String name) {
            this.name = name;
        }

    }

}