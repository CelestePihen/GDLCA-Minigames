package fr.cel.valocraft.arena;

import fr.cel.gameapi.scoreboard.GameScoreboard;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.valocraft.arena.state.ArenaState;
import fr.cel.valocraft.arena.state.game.PlayingArenaState;
import fr.cel.valocraft.arena.state.game.SpikeArenaState;
import fr.cel.valocraft.arena.state.game.TimeOverArenaState;
import fr.cel.valocraft.arena.state.game.WaitingArenaState;
import fr.cel.valocraft.arena.state.pregame.InitArenaState;
import fr.cel.valocraft.arena.state.pregame.PreGameArenaState;
import fr.cel.valocraft.arena.state.pregame.StartingArenaState;
import fr.cel.valocraft.manager.GameManager;
import fr.cel.valocraft.manager.Role;
import fr.cel.valocraft.manager.ValoTeam;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class ValoArena {

    private final GameManager gameManager;

    private final String nameArena;
    private final String displayName;

    private final Location spawnLoc;
    private final Location attackersSpawn;
    private final Location defendersSpawn;

    private final List<UUID> players;
    private final List<UUID> spectators;

    private ArenaState arenaState;

    private final GameScoreboard scoreboard;

    private final BossBar bossBar;

    // TODO
    private final ArenaConfig arenaConfig;
    private final List<Location> invisibleBarriersLocations;

    private Role attackers;
    private Role defenders;

    private ValoTeam redTeam;
    private ValoTeam blueTeam;

    private int globalRound;

    @Setter private Block spike;

    public ValoArena(String nameArena, String displayName, Location spawnLoc, Location attackersSpawn, Location defenderSpawn, ArenaConfig arenaConfig, GameManager gameManager) {
        this.nameArena = nameArena;
        this.displayName = displayName;

        this.spawnLoc = spawnLoc;
        this.attackersSpawn = attackersSpawn;
        this.defendersSpawn = defenderSpawn;

        this.scoreboard = new GameScoreboard("valo" + nameArena.substring(0, 2));

        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();

        this.bossBar = BossBar.bossBar(
                Component.text("0", NamedTextColor.BLUE)
                        .append(Component.text(" | 1 | ", NamedTextColor.WHITE))
                        .append(Component.text("0", NamedTextColor.RED)),
                1.0F, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

        this.arenaState = new InitArenaState(this);
        this.globalRound = 1;

        this.arenaConfig = arenaConfig;
        this.invisibleBarriersLocations = arenaConfig.getLocationInvisibleBarrier();

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
            player.sendMessage(gameManager.getPrefix().append(Component.text("Vous avez été mis(e) en spectateur sur " + this.displayName)));
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

        player.teleport(spawnLoc);
        player.showTitle(Title.title(Component.text("Valocraft", NamedTextColor.GOLD), Component.text(displayName)));
        player.getInventory().clear();
        player.setGameMode(gameMode);

        if (gameMode == GameMode.SPECTATOR) {
            spectators.add(player.getUniqueId());
            player.sendMessage(gameManager.getPrefix().append(Component.text("La partie est déjà lancée.")));
        } else {
            sendMessage(player.displayName().append(Component.text(" a rejoint la partie !")));
            player.getInventory().setItem(4, new ItemBuilder(Material.WHITE_WOOL).itemName(Component.text("Sélecteur d'équipes")).addLoreLine(Component.text("Sélectionner votre équipe.", NamedTextColor.GREEN)).toItemStack());
        }
    }

    /**
     * Permet de lancer la partie
     * @return Retourne si la partie se lance
     */
    public boolean startGame() {
        // on regarde s'il y a au moins 2 joueurs dans la partie, qu'il y a au moins 1 joueur dans les 2 teams et on regarde si la partie est prête à se lancer
        if (players.size() >= 2 && (!blueTeam.getPlayers().isEmpty() || !redTeam.getPlayers().isEmpty()) && getArenaState() instanceof PreGameArenaState) {
            setArenaState(new StartingArenaState(this));
            return true;
        }
        return false;
    }

    /**
     * Permet de finir la partie
      */
    public void endGame() {
        sendWinnerMessage();

        blueTeam.resetTeam();
        redTeam.resetTeam();

        players.forEach(uuid -> scoreboard.removePlayer(Bukkit.getPlayer(uuid)));

        // Reset Bossbar
        removePlayersToBossBar();
        bossBar.name(Component.text("0", NamedTextColor.BLUE)
                .append(Component.text(" | 1 | ", NamedTextColor.WHITE))
                .append(Component.text("0", NamedTextColor.RED))).progress(1.0F);

        sendPlayersToHub();

        players.clear();
        spectators.clear();

        attackers = new Role("attackers", "Attaquants", attackersSpawn, scoreboard.registerTeam("a" + nameArena, NamedTextColor.RED));
        defenders = new Role("defenders", "Défenseurs", defendersSpawn, scoreboard.registerTeam("d" + nameArena, NamedTextColor.BLUE));

        redTeam = new ValoTeam("redTeam", "Équipe Rouge", attackers);
        blueTeam = new ValoTeam("blueTeam", "Équipe Bleue", defenders);

        setArenaState(new InitArenaState(this));
    }

    public void removePlayer(Player player) {
        if (!isPlayerInArena(player)) return;
        players.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());

        getBlueTeam().removePlayer(player);
        getRedTeam().removePlayer(player);

        scoreboard.removePlayer(player);
        player.hideBossBar(bossBar);

        if (getPlayers().size() < 2 || (getBlueTeam().getPlayers().isEmpty() || getRedTeam().getPlayers().isEmpty())) {

            Component gameCancelled = Component.text("Partie annulé... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour jouer.");

            if (arenaState instanceof PreGameArenaState) return;

            else if (arenaState instanceof StartingArenaState startingArenaState) {
                startingArenaState.getArenaStartingTask().cancel();
                sendMessage(Component.text("Démarrage annulé... Vous avez besoin d'au moins 2 joueurs et d'au moins 1 joueur dans chaque équipe pour lancer."));
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

            else if (arenaState instanceof SpikeArenaState spikeArenaState) {
                spikeArenaState.getSpikeArenaTask().cancel();
                sendMessage(gameCancelled);
            }

            else if (arenaState instanceof TimeOverArenaState timeOverArenaState) {
                timeOverArenaState.getTimeOverArenaTask().cancel();
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
        // TODO mettre le joueur en spectateur sur un allié et ne lui permettre d'aller que sur ses alliés
        // player.setSpectatorTarget();
        player.sendMessage(gameManager.getPrefix().append(Component.text("Vous êtes mort(e).")));

        checkRound();
    }

    /**
     * Permet de vérifier si une équipe a gagné le round.
     * Si on est en TimeOver (entre deux manches), on ne fait rien.
     * Si on est en SpikeArenaState et que les attaquants sont tous en spectateur, on ne fait rien (évite de donner un round aux défenseurs si le spike a explosé et que les attaquants sont tous morts).
     * Si une équipe est toute en spectateur, l'autre équipe gagne le round.
     * On affiche le score, on incrémente le round global et on vérifie si une équipe a gagné la partie
     */
    private void checkRound() {
        if (this.arenaState instanceof TimeOverArenaState) return;

        if (arenaState instanceof SpikeArenaState && getTeamByRole(attackers).isAllTeamInSpec()) return;

        if (getBlueTeam().isAllTeamInSpec()) {
            getRedTeam().setRoundWin(getRedTeam().getRoundWin() + 1);
        } else if (getRedTeam().isAllTeamInSpec()) {
            getBlueTeam().setRoundWin(getBlueTeam().getRoundWin() + 1);
        } else {
            return;
        }

        showTeamRound();
        globalRound += 1;
        checkWin();
    }

    /**
     * Permet de vérifier si une équipe a gagné la partie (13 rounds gagnés).
     * Si oui, on termine la partie.
     * Sinon, on passe en TimeOverArenaState.
     * Si les attaquants ont posé le spike ET ont tué les défenseurs, on annule la tâche du spike
     */
    private void checkWin() {
        if (blueTeam.getRoundWin() >= 13 || redTeam.getRoundWin() >= 13) {
            // TODO mettre un GameState de Win entre la fin et le renvoi au Hub genre avec les vainqueurs affichés / classements avec kills, etc.
            endGame();
            return;
        }

        if (arenaState instanceof SpikeArenaState spikeArenaState) spikeArenaState.getSpikeArenaTask().cancel();

        setArenaState(new TimeOverArenaState(this));
    }

    /**
     * Permet d'inverser les équipes attaquantes et défenseuses quand on atteint la manche 13 (global)
     */
    public void inverseTeam() {
        if (getBlueTeam().getRole() == getAttackers()) {
            getBlueTeam().setRole(defenders);
            getRedTeam().setRole(attackers);
        } else {
            getBlueTeam().setRole(attackers);
            getRedTeam().setRole(defenders);
        }
    }

    /**
     * Permet d'ajouter un round aux attaquants (quand le spike a explosé)
     */
    public void addSpikeRoundSpike() {
        ValoTeam valoTeam = getTeamByRole(attackers);
        valoTeam.setRoundWin(valoTeam.getRoundWin() + 1);

        addGlobalRoundEndRound();
        setArenaState(new TimeOverArenaState(this));
    }

    /**
     * Permet d'ajouter un round aux défenseurs.
     * Peut arriver quand les défenseurs désamorcent le spike ou que le temps est écoulé (en mode playing)
     */
    public void addRoundDefender() {
        ValoTeam valoTeam = getTeamByRole(defenders);
        valoTeam.setRoundWin(valoTeam.getRoundWin() + 1);

        addGlobalRoundEndRound();
    }

    public void sendMessage(Component message) {
        message = gameManager.getPrefix().append(message);
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.sendMessage(message);
        }
    }

    public void sendTitle(Component title, Component subtitle) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.showTitle(Title.title(title, subtitle));
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

    public void clearPlayerInventories() {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.getInventory().clear();
        }
    }

    public void setGameModePlayers(GameMode gameMode) {
        for (UUID pls : getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) {
                if (spectators.contains(pls)) player.setGameMode(GameMode.SPECTATOR);
                else player.setGameMode(gameMode);
            }
        }
    }

    public Role getRoleByPlayer(Player player) {
        for (Role role : Arrays.asList(attackers, defenders)) {
            if (role.team().getPlayers().contains(player.getName())) return role;
        }
        return null;
    }

    public void showTeamRound() {
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendActionBar(Component.text(getBlueTeam().getRoundWin(), NamedTextColor.BLUE)
                        .append(Component.text(" | ", NamedTextColor.WHITE))
                        .append(Component.text(getRedTeam().getRoundWin(), NamedTextColor.RED)));
            }
        }

        bossBar.name(Component.text(getBlueTeam().getRoundWin(), NamedTextColor.BLUE)
                .append(Component.text(" | " + getGlobalRound() + " | ", NamedTextColor.WHITE))
                .append(Component.text(getRedTeam().getRoundWin(), NamedTextColor.RED)));
    }

    public void removePlayersToBossBar() {
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.hideBossBar(this.bossBar);
        }
    }

    private ValoTeam getTeamByRole(Role role) {
        for (ValoTeam team : Arrays.asList(redTeam, blueTeam)) {
            if (team.getRole() == role) return team;
        }
        return redTeam;
    }

    private void addGlobalRoundEndRound() {
        showTeamRound();
        globalRound += 1;
        checkWin();
    }

    private void sendPlayersToHub() {
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) gameManager.getPlayerManager().sendPlayerToHub(player);
        }
    }

    private void sendWinnerMessage() {
        if (getBlueTeam().getRoundWin() >= 13) {
            sendMessage(Component.text("L'équipe bleue ", NamedTextColor.BLUE).append(Component.text("remporte la partie !", NamedTextColor.WHITE)));
        }

        else if (getRedTeam().getRoundWin() >= 13) {
            sendMessage(Component.text("L'équipe rouge ", NamedTextColor.RED).append(Component.text("remporte la partie !", NamedTextColor.WHITE)));
        }

        else sendMessage(Component.text("Égalité ! Personne ne remporte la partie."));
    }

}