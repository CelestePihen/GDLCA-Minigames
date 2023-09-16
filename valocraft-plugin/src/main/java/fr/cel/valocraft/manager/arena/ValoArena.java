package fr.cel.valocraft.manager.arena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import fr.cel.hub.Hub;
import fr.cel.valocraft.manager.ValoGameManager;
import fr.cel.valocraft.manager.Role;
import fr.cel.valocraft.manager.ValoTeam;
import fr.cel.valocraft.manager.arena.state.pregame.InitArenaState;
import fr.cel.valocraft.manager.arena.state.pregame.PreGameArenaState;
import fr.cel.valocraft.manager.arena.state.pregame.StartingArenaState;
import fr.cel.valocraft.manager.arena.state.ArenaState;
import fr.cel.valocraft.manager.arena.state.game.PlayingArenaState;
import fr.cel.valocraft.manager.arena.state.game.SpikeArenaState;
import fr.cel.valocraft.manager.arena.state.game.TimeOverArenaState;
import fr.cel.valocraft.manager.arena.state.game.WaitingArenaState;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
public class ValoArena {
    
    // GameManager
    private final ValoGameManager gameManager;

    // Names
    private final String nameArena;
    private final String displayName;

    // ArenaState
    private ArenaState arenaState;

    // Locations
    private final Location spawnLoc;
    private final Location attackersSpawn;
    private final Location defendersSpawn;

    // Roles
    private final Role attackers;
    private final Role defenders;

    // Team Lists
    private final List<UUID> players;
    private final List<UUID> spectators;
    private final ValoTeam redTeam;
    private final ValoTeam blueTeam;

    // Rounds
    private int globalRound;

    // Spike
    @Setter private Block spike;

    // Scoreboard
    private final JGlobalMethodBasedScoreboard scoreboard;

    private final BossBar bossBar;

    // Constructor
    public ValoArena(String nameArena, String displayName, Location spawnLoc, Location attackersSpawn, Location defenderSpawn, ValoGameManager gameManager) {
        // Names
        this.nameArena = nameArena;
        this.displayName = displayName;

        // Locations
        this.spawnLoc = spawnLoc;
        this.attackersSpawn = attackersSpawn;
        this.defendersSpawn = defenderSpawn;

        // Scoreboard
        this.scoreboard = new JGlobalMethodBasedScoreboard();

        // Liste
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();

        this.attackers = new Role("attackers", "Attaquants", getAttackersSpawn(), this.scoreboard, this.scoreboard.createTeam("a" + getNameArena(), "", ChatColor.RED));
        this.defenders = new Role("defenders", "Défenseurs", getDefendersSpawn(), this.scoreboard, this.scoreboard.createTeam("d" + getNameArena(), "", ChatColor.BLUE));
        
        this.redTeam = new ValoTeam("redTeam", "Équipe Rouge", this.attackers);
        this.blueTeam = new ValoTeam("blueTeam", "Équipe Bleue", this.defenders);

        this.bossBar = Bukkit.createBossBar(ChatUtility.format("&10 " + "&r| 1 " + "&r| &c 0"), BarColor.PURPLE, BarStyle.SOLID);

        // ArenaState / Global Round
        this.arenaState = new InitArenaState(this);
        this.globalRound = 1;

        this.gameManager = gameManager;
    }

    public boolean isPlayerInArena(Player player) {
        return players.contains(player.getUniqueId());
    }

    public int getRoundWinBlue() {
        return getBlueTeam().getRoundWin();
    }

    public int getRoundWinRed() {
        return getRedTeam().getRoundWin();
    }

    public void setArenaState(ArenaState arenaState) {
        if (this.arenaState != null) this.arenaState.onDisable();
        this.arenaState = arenaState;
        this.arenaState.onEnable(gameManager.getMain());
    }

    public void addPlayer(Player player) {
        if (players.contains(player.getUniqueId())) return;

        if (arenaState instanceof StartingArenaState || arenaState instanceof WaitingArenaState || arenaState instanceof PlayingArenaState || arenaState instanceof SpikeArenaState || arenaState instanceof TimeOverArenaState) {
            player.sendMessage(gameManager.getPrefix() + "Vous avez été mis(e) en spectateur sur " + this.getDisplayName() + ".");
            join(player, GameMode.SPECTATOR, false);
        }

        else if (arenaState instanceof PreGameArenaState) {
            join(player, GameMode.ADVENTURE, true);
        }

        else {
            setArenaState(new PreGameArenaState(this));
            join(player, GameMode.ADVENTURE, true);
        }
    }

    private void join(Player player, GameMode gameMode, boolean joinMessage) {
        gameManager.getPlayerManager().removePlayerInHub(player);
        players.add(player.getUniqueId());
        this.scoreboard.addPlayer(player);

        if (joinMessage) {
            sendMessage(player.getDisplayName() + " a rejoint la partie !");
        }

        player.teleport(this.getSpawnLoc());

        player.sendTitle(ChatUtility.format("&6ValoCraft"), getDisplayName(), 10, 70, 20);
        player.getInventory().clear();
        player.setGameMode(gameMode);
        player.getInventory().setItem(4, new ItemBuilder(Material.WHITE_WOOL).setDisplayName("Sélecteur d'équipes").addLoreLine("&aSélectionner votre équipe.").toItemStack());
    }

    public void removePlayer(Player player) {
        if (!players.contains(player.getUniqueId())) return;
        players.remove(player.getUniqueId());

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
        Bukkit.getScheduler().scheduleSyncDelayedTask(gameManager.getMain(), () -> player.spigot().respawn(), 10);
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(gameManager.getPrefix() + "Vous êtes mort(e).");

        checkRound();
    }

    public void inverseTeam() {
        if (getBlueTeam().getRole() == getAttackers()) getBlueTeam().setRole(defenders);
        else getBlueTeam().setRole(attackers);

        if (getRedTeam().getRole() == getAttackers()) getRedTeam().setRole(defenders);
        else getRedTeam().setRole(attackers);
    }

    public void sendMessage(String message) {
        message = ChatUtility.format(gameManager.getPrefix() + message);
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.sendMessage(message);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.sendTitle(ChatUtility.format(title), ChatUtility.format(subtitle), 10, 70, 20);
        }
    }

    public void playSound(Sound sound) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    public void setLevel(int timer) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setLevel(timer);
        }
    }

    public void clearPlayers() {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.getInventory().clear();
        }
    }

    public void setGameModePlayers(GameMode gameMode) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.setGameMode(gameMode);
        }
    }

    public void addRoundSpike() {
        ValoTeam valoTeam = getTeamByRole(attackers);
        valoTeam.setRoundWin(valoTeam.getRoundWin() + 1);

        showTeamRound();
        globalRound += 1;
        checkWin();
    }

    // Spike Defused / End of Time (PlayingState)
    public void addRoundDefender() {
        getTeamByRole(defenders).setRoundWin(getTeamByRole(defenders).getRoundWin() + 1);
        showTeamRound();
        globalRound += 1;
        checkWin();
    }

    public ValoTeam getTeamByRole(Role role) {
        List<ValoTeam> valoTeam = Arrays.asList(redTeam, blueTeam);
        for (ValoTeam team : valoTeam) {
            if (team.getRole() == role) return team;
        }
        return null;
    }

    public Role getRoleByPlayer(Player player) {
        List<Role> roles = Arrays.asList(attackers, defenders);
        for (Role role : roles) {
            if (role.getTeam().isOnTeam(player.getUniqueId())) return role;
        }
        return null;
    }

    public void showTeamRound() {
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                TextComponent str = new TextComponent(ChatUtility.format("&1" + getRoundWinBlue() + "&r&f | " + "&c" + getRoundWinRed()));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, str);
                bossBar.setTitle(ChatUtility.format("&1" + getRoundWinBlue() + "&r | " + getGlobalRound() + "&f | &c" + getRoundWinRed()));
            }
        }
    }

    private void checkRound() {
        if (getBlueTeam().isTeamInSpec()) getRedTeam().setRoundWin(getRedTeam().getRoundWin() + 1);
        else if (getRedTeam().isTeamInSpec()) getBlueTeam().setRoundWin(getBlueTeam().getRoundWin() + 1);
        else return;

        showTeamRound();
        globalRound += 1;
        checkWin();
    }

    private void checkWin() {
        if (blueTeam.getRoundWin() >= 13 || redTeam.getRoundWin() >= 13) {
            setArenaState(new InitArenaState(this));
            sendWinnerMessage();
            getBlueTeam().clearPlayers();
            getRedTeam().clearPlayers();

            // reset les couleurs des teams
            getAttackers().getTeam().toBukkitTeam(getScoreboard().toBukkitScoreboard()).setColor(ChatColor.RED);
            getDefenders().getTeam().toBukkitTeam(getScoreboard().toBukkitScoreboard()).setColor(ChatColor.BLUE);

            players.forEach(uuid -> scoreboard.removePlayer(Bukkit.getPlayer(uuid)));
            removePlayersToBossBar();
            sendPlayersToHub();
            players.clear();
        }
        
        else if (arenaState instanceof SpikeArenaState spikeArenaState) {
            spikeArenaState.getSpikeArenaTask().cancel();
        }

        else {
            setArenaState(new TimeOverArenaState(this));
        }
    }

    private void sendPlayersToHub() {
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) Hub.getHub().getPlayerManager().sendPlayerToHub(player);
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

    private void removePlayersToBossBar() {
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            getBossBar().removePlayer(player);
        }
    }

}