package fr.cel.valocraft.arena;

import java.util.*;

import fr.cel.gameapi.scoreboard.GameScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import fr.cel.valocraft.manager.GameManager;
import fr.cel.valocraft.manager.Role;
import fr.cel.valocraft.manager.ValoTeam;
import fr.cel.valocraft.arena.state.pregame.InitArenaState;
import fr.cel.valocraft.arena.state.pregame.PreGameArenaState;
import fr.cel.valocraft.arena.state.pregame.StartingArenaState;
import fr.cel.valocraft.arena.state.ArenaState;
import fr.cel.valocraft.arena.state.game.PlayingArenaState;
import fr.cel.valocraft.arena.state.game.SpikeArenaState;
import fr.cel.valocraft.arena.state.game.TimeOverArenaState;
import fr.cel.valocraft.arena.state.game.WaitingArenaState;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
public class ValoArena {

    private final GameManager gameManager;

    private final String nameArena;
    private final String displayName;

    private ArenaState arenaState;

    private final Location spawnLoc;
    private final Location attackersSpawn;
    private final Location defendersSpawn;

    private final Set<UUID> players;
    private final Set<UUID> spectators;

    @Setter private Role attackers;
    @Setter private Role defenders;

    @Setter private ValoTeam redTeam;
    @Setter private ValoTeam blueTeam;

    private int globalRound;

    @Setter private Block spike;

    private final GameScoreboard scoreboard;

    private final BossBar bossBar;

    public ValoArena(String nameArena, String displayName, Location spawnLoc, Location attackersSpawn, Location defenderSpawn, GameManager gameManager) {
        this.nameArena = nameArena;
        this.displayName = displayName;

        this.spawnLoc = spawnLoc;
        this.attackersSpawn = attackersSpawn;
        this.defendersSpawn = defenderSpawn;

        this.scoreboard = new GameScoreboard("Valocraft");

        this.players = new HashSet<>();
        this.spectators = new HashSet<>();

        this.bossBar = Bukkit.createBossBar(ChatUtility.format("&10 " + "&r| 1 " + "&r| &c 0"), BarColor.PURPLE, BarStyle.SOLID);

        this.arenaState = new InitArenaState(this);
        this.globalRound = 1;

        this.gameManager = gameManager;
    }

    public boolean isPlayerInArena(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void setArenaState(ArenaState arenaState) {
        if (this.arenaState != null) this.arenaState.onDisable();
        this.arenaState = arenaState;
        this.arenaState.onEnable(gameManager.getMain());
    }

    /**
     * Permet d'ajouter un joueur à l'arène (fonctionne avec la méthode join)
     * @param player Le joueur à ajouter
     */
    public void addPlayer(Player player) {
        if (isPlayerInArena(player)) return;

        if (arenaState instanceof StartingArenaState || arenaState instanceof WaitingArenaState || arenaState instanceof PlayingArenaState || arenaState instanceof SpikeArenaState || arenaState instanceof TimeOverArenaState) {
            player.sendMessage(gameManager.getPrefix() + "Vous avez été mis(e) en spectateur sur " + this.getDisplayName() + ".");
            join(player, GameMode.SPECTATOR);
        }

        else if (arenaState instanceof PreGameArenaState) {
            join(player, GameMode.ADVENTURE);
        }

        else {
            setArenaState(new PreGameArenaState(this));
            join(player, GameMode.ADVENTURE);
        }
    }

    /**
     * Permet de faire rejoindre le joueur à l'arène
     * @param player Le joueur à faire rejoindre
     * @param gameMode Le mode de jeu à mettre au joueur
     */
    private void join(Player player, GameMode gameMode) {
        gameManager.getPlayerManager().removePlayerInHub(player);
        players.add(player.getUniqueId());
        scoreboard.addPlayer(player);

        player.teleport(getSpawnLoc());
        player.sendTitle(ChatUtility.format("&6ValoCraft"), getDisplayName(), 10, 70, 20);
        player.getInventory().clear();
        player.setGameMode(gameMode);

        if (gameMode == GameMode.SPECTATOR) {
            spectators.add(player.getUniqueId());
        } else {
            sendMessage(player.getDisplayName() + " a rejoint la partie !");
            player.getInventory().setItem(4, new ItemBuilder(Material.WHITE_WOOL).setDisplayName("Sélecteur d'équipes").addLoreLine("§aSélectionner votre équipe.").toItemStack());
        }
    }

    /**
     * Permet de lancer la partie
     * @return Retourne si la partie se lance
     */
    public boolean startGame() {
        // on regarde s'il y a au moins 2 joueurs dans la partie, qu'il y a au moins 1 joueur dans les 2 teams et on regarde si la partie est prête à se lancer
        if (getPlayers().size() >= 2 && (!getBlueTeam().getPlayers().isEmpty() || !getRedTeam().getPlayers().isEmpty()) && getArenaState() instanceof PreGameArenaState) {
            setArenaState(new StartingArenaState(this));
            return true;
        }
        return false;
    }

    /**
     * Permet de finir la partie
      */
    public void endGame() {
        // on envoie un message avec les vainqueurs
        sendWinnerMessage();

        // on nettoie les teams
        getBlueTeam().clearPlayers();
        getRedTeam().clearPlayers();

        // on enlève les joueurs du scoreboard
        players.forEach(uuid -> scoreboard.removePlayer(Bukkit.getPlayer(uuid)));


        // on leur enlève la bossbar
        removePlayersToBossBar();

        // on les envoie au hub
        sendPlayersToHub();

        // on enlève tous les joueurs et les spectateurs de l'arène
        players.clear();
        spectators.clear();

        // on remet l'état de l'arène en Init
        setArenaState(new InitArenaState(this));
    }

    public void removePlayer(Player player) {
        if (!isPlayerInArena(player)) return;
        players.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());

        getBlueTeam().removePlayer(player);
        getRedTeam().removePlayer(player);

        scoreboard.removePlayer(player);
        bossBar.removePlayer(player);

        if (getPlayers().size() < 2 || (getBlueTeam().getPlayers().isEmpty() || getRedTeam().getPlayers().isEmpty())) {

            String gameCancelled = "Partie annulé... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.";

            if (arenaState instanceof PreGameArenaState) return;

            else if (arenaState instanceof StartingArenaState startingArenaState) {
                startingArenaState.getArenaStartingTask().cancel();
                sendMessage("Démarrage annulé... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour lancer.");
                setArenaState(new PreGameArenaState(this));
            }
            
            else if (arenaState instanceof WaitingArenaState waitingArenaState) {
                waitingArenaState.getWaitingArenaTask().cancel();
                sendMessage(gameCancelled);
            }
            
            else if (arenaState instanceof PlayingArenaState playingArenaState) {
                playingArenaState.getPlayingArenaTask().cancel();
                sendMessage(gameCancelled);
            }
            
            else if (arenaState instanceof TimeOverArenaState timeOverArenaState) {
                timeOverArenaState.getTimeOverArenaTask().cancel();
                sendMessage(gameCancelled);
            }
            
            else if (arenaState instanceof SpikeArenaState spikeArenaState) {
                spikeArenaState.getSpikeArenaTask().cancel();
                sendMessage(gameCancelled);
            }

            if (getBlueTeam().getPlayers().isEmpty()) getRedTeam().setRoundWin(13);
            else if (getRedTeam().getPlayers().isEmpty()) getBlueTeam().setRoundWin(13);
            checkWin();
        }
    }

    public void eliminate(Player player) {
        player.teleport(getSpawnLoc());
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(gameManager.getPrefix() + "Vous êtes mort(e).");

        checkRound();
    }

    private void checkRound() {
        if (getBlueTeam().isAllTeamInSpec()) {
            getRedTeam().setRoundWin(getRedTeam().getRoundWin() + 1);
        } else if (getRedTeam().isAllTeamInSpec()) {
            getBlueTeam().setRoundWin(getBlueTeam().getRoundWin() + 1);
        } else {
            return;
        }

        if (arenaState instanceof SpikeArenaState && getTeamByRole(attackers).isAllTeamInSpec()) {
            return;
        }

        showTeamRound();
        globalRound += 1;
        checkWin();
    }

    private void checkWin() {
        if (blueTeam.getRoundWin() >= 13 || redTeam.getRoundWin() >= 13) {
            // TODO mettre un GameState de Win entre la fin et le renvoi au Hub genre avec les vainqueurs affichés / classements
            Bukkit.getScheduler().runTaskLater(gameManager.getMain(), this::endGame, 20);
            return;
        }

        if (arenaState instanceof SpikeArenaState spikeArenaState) {
            spikeArenaState.getSpikeArenaTask().cancel();
        }

        setArenaState(new TimeOverArenaState(this));
    }

    public void inverseTeam() {
        if (getBlueTeam().getRole() == getAttackers()) {
            getBlueTeam().setRole(defenders);
            getRedTeam().setRole(attackers);
        } else {
            getBlueTeam().setRole(attackers);
            getRedTeam().setRole(defenders);
        }
    }

    public void addRoundSpike() {
        ValoTeam valoTeam = getTeamByRole(attackers);
        valoTeam.setRoundWin(valoTeam.getRoundWin() + 1);

        addGlobalRoundEndRound();
        setArenaState(new TimeOverArenaState(this));
    }

    // Spike Defused / End of Time (when PlayingState)
    public void addRoundDefender() {
        ValoTeam valoTeam = getTeamByRole(defenders);
        valoTeam.setRoundWin(valoTeam.getRoundWin() + 1);

        addGlobalRoundEndRound();
    }

    public void sendMessage(String message) {
        message = ChatUtility.format(gameManager.getPrefix() + message);
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.sendMessage(message);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.sendTitle(ChatUtility.format(title), ChatUtility.format(subtitle), 10, 70, 20);
        }
    }

    public void playSound(Sound sound) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    public void setLevel(int timer) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.setLevel(timer);
        }
    }

    public void clearPlayers() {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.getInventory().clear();
        }
    }

    public void setGameModePlayers(GameMode gameMode) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.setGameMode(gameMode);
        }
    }

    public ValoTeam getTeamByRole(Role role) {
        for (ValoTeam team : Arrays.asList(redTeam, blueTeam)) {
            if (team.getRole() == role) return team;
        }
        return null;
    }

    public Role getRoleByPlayer(Player player) {
        for (Role role : Arrays.asList(attackers, defenders)) {
            if (role.getTeam().getPlayers().contains(player.getName())) return role;
        }
        return null;
    }

    public void showTeamRound() {
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            TextComponent str = new TextComponent(ChatUtility.format("&1" + getBlueTeam().getRoundWin() + "&r&f | " + "&c" + getRedTeam().getRoundWin()));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, str);

            bossBar.setTitle(ChatUtility.format("&1" + getBlueTeam().getRoundWin() + "&r | " + getGlobalRound() + "&f | &c" + getRedTeam().getRoundWin()));
        }
    }

    public void removePlayersToBossBar() {
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            getBossBar().removePlayer(player);
        }
    }

    private void addGlobalRoundEndRound() {
        showTeamRound();
        globalRound += 1;
        checkWin();
    }

    private void sendPlayersToHub() {
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            gameManager.getPlayerManager().sendPlayerToHub(player);
        }
    }

    private void sendWinnerMessage() {
        if (getBlueTeam().getRoundWin() >= 13) {
            sendMessage("&bL'équipe bleue &rremporte la partie !");
            return;
        }

        if (getRedTeam().getRoundWin() >= 13) {
            sendMessage("&cL'équipe rouge &rremporte la partie !");
            return;
        }

        sendMessage("Égalité ! Personne ne remporte la partie.");
    }

}