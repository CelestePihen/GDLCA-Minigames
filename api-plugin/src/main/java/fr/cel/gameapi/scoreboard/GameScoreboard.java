package fr.cel.gameapi.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class GameScoreboard {

    private final String name;

    private final Scoreboard scoreboard;

    private final List<GameTeam> gameTeams;
    private final List<UUID> playersUUID;

    public GameScoreboard(String name) {
        this.name = name;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.gameTeams = new ArrayList<>();
        this.playersUUID = new ArrayList<>();
    }

    /**
     * Crée une nouvelle équipe pour le scoreboard (GameTeam)
     * @param teamName Le nom de l'équipe
     * @param color La couleur de l'équipe
     * @return Retourne la GameTeam
     */
    public GameTeam registerTeam(String teamName, ChatColor color) {
        return new GameTeam(teamName, color, this.scoreboard);
    }

    /**
     * Ajoute le joueur au scoreboard
     * @param player Le joueur à ajouter
     */
    public void addPlayer(Player player) {
        playersUUID.add(player.getUniqueId());
        player.setScoreboard(scoreboard);
    }

    /**
     * Retire le joueur du scoreboard et le remet sur le scoreboard général
     * @param player Le joueur à retirer
     */
    public void removePlayer(Player player) {
        for (GameTeam gameTeam : gameTeams) {
            gameTeam.removePlayer(player);
        }

        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playersUUID.remove(player.getUniqueId());
    }

    public void resetScoreboard() {
        for (UUID uuid : playersUUID) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            removePlayer(player);
        }
    }

}