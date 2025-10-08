package fr.cel.gameapi.scoreboard;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
     * Creates a new team for the scoreboard (GameTeam)
     * @param teamName The name of the team
     * @param color The color of the team
     * @return Returns the created GameTeam
     */
    public GameTeam registerTeam(String teamName, NamedTextColor color) {
        return new GameTeam(teamName, color, this.scoreboard);
    }

    /**
     * Adds a player to the scoreboard
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        playersUUID.add(player.getUniqueId());
        player.setScoreboard(scoreboard);
    }

    /**
     * Removes a player from the scoreboard and sets their scoreboard back to the main scoreboard
     * @param player The player to remove
     */
    public void removePlayer(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    /**
     * Resets the scoreboard by removing all players and clearing their teams
     */
    public void resetScoreboard() {
        for (GameTeam gameTeam : this.gameTeams) {
            gameTeam.clearTeam();
        }

        for (UUID uuid : playersUUID) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            removePlayer(player);
        }

        playersUUID.clear();
    }

}