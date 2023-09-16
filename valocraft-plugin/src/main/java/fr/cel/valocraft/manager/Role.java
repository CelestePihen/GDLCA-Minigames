package fr.cel.valocraft.manager;

import org.bukkit.Location;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lombok.Getter;

@Getter
public class Role {

    private final String name;
    private final String displayName;
    private final Location spawn;
    private final JScoreboardTeam team;

    public Role(String name, String displayName, Location spawn, JGlobalMethodBasedScoreboard scoreboard, JScoreboardTeam team) {
        this.name = name;
        this.displayName = displayName;
        this.spawn = spawn;
        this.team = team;

        team.toBukkitTeam(scoreboard.toBukkitScoreboard()).setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
        team.toBukkitTeam(scoreboard.toBukkitScoreboard()).setAllowFriendlyFire(false);
    }

}