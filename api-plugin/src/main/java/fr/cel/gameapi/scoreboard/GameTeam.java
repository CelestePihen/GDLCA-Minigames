package fr.cel.gameapi.scoreboard;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class GameTeam {

    private final String name;
    private final Team team;

    public GameTeam(String name, NamedTextColor color, Scoreboard scoreboard) {
        this.name = name;
        this.team = scoreboard.registerNewTeam(name);
        this.team.color(color);
    }

    /**
     * Adds a player to the team.
     * <p>Note: Adding a player to this team will remove them from any other team on the same scoreboard.</p>
     * @param player The player to add. Must be online.
     */
    public void addPlayer(Player player) {
        if (player == null || !player.isOnline() || containsPlayer(player)) return;
        team.addPlayer(player);
    }

    /**
     * Removes a player from the team.
     * @param player The player to remove. Must be online.
     */
    public void removePlayer(Player player) {
        if (player == null || !player.isOnline() || !containsPlayer(player)) return;
        team.removeEntry(player.getName());
    }

    /**
     * Removes all players from the team
     */
    public void clearTeam() {
        for (String entry : team.getEntries()) team.removeEntry(entry);
    }

    /**
     * Checks if a player is in the team
     * @param player The player to check
     * @return Returns true if the player is in the team
     */
    public boolean containsPlayer(Player player) {
        if (player == null || !player.isOnline()) return false;
        return team.hasPlayer(player);
    }

    /**
     * Gets all player names in the team
     * @return Returns a set of all player names
     */
    // TODO: Rename to getPlayerNames() or getEntries()
    public Set<String> getPlayers() {
        return team.getEntries();
    }

    /**
     * Gets all player UUIDs in the team
     * @return Returns a set of all player UUIDs
     */
    public Set<UUID> getPlayerUUIDs() {
        Set<UUID> playerUUIDs = new HashSet<>();

        for (String entry : team.getEntries()) {
            Player player = Bukkit.getPlayer(entry);
            if (player != null) playerUUIDs.add(player.getUniqueId());
        }

        return playerUUIDs;
    }

    /**
     * Gets all online players in the team
     * @return Returns a set of all online players
     */
    public Set<Player> getOnlinePlayers() {
        Set<Player> players = new HashSet<>();

        for (String entry : team.getEntries()) {
            Player player = Bukkit.getPlayer(entry);
            if (player != null) players.add(player);
        }

        return players;
    }

    /**
     * Sets the collision behavior with players
     * @param optionStatus The collision status
     */
    public void setCollisionRule(Team.OptionStatus optionStatus) {
        team.setOption(Team.Option.COLLISION_RULE, optionStatus);
    }

    /**
     * Sets the visibility of death messages
     * @param optionStatus The status for death message visibility
     */
    public void setDeathMessageVisibility(Team.OptionStatus optionStatus) {
        team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, optionStatus);
    }

    /**
     * Sets the visibility of the NameTag (player name above the head)
     * @param optionStatus The status for NameTag visibility
     */
    public void setNameTagVisibility(Team.OptionStatus optionStatus) {
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, optionStatus);
    }

    /**
     * Sets whether team members can see invisible players
     * @param canSeeFriendlyInvisibles If true, teammates can see each other while invisible
     */
    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        team.setCanSeeFriendlyInvisibles(canSeeFriendlyInvisibles);
    }

    /**
     * Sets whether players on the same team can hit each other
     * @param allowFriendlyFire If true, teammates can damage each other
     */
    public void setAllowFriendlyFire(boolean allowFriendlyFire) {
        team.setAllowFriendlyFire(allowFriendlyFire);
    }

    /**
     * Gets the number of players in the team
     * @return Returns the number of players
     */
    public int getSize() {
        return team.getSize();
    }

}