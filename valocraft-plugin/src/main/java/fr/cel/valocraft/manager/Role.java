package fr.cel.valocraft.manager;

import org.bukkit.Location;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lombok.Getter;

public class Role {

    @Getter private String name;
    @Getter private String displayName;

    @Getter private Location spawn;
    
    @Getter private JScoreboardTeam team;

    public Role(String name, String displayName, Location spawn, JGlobalMethodBasedScoreboard scoreboard, JScoreboardTeam team) {
        this.name = name;
        this.displayName = displayName;

        this.spawn = spawn;

        this.team = team;

        team.toBukkitTeam(scoreboard.toBukkitScoreboard()).setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
        team.toBukkitTeam(scoreboard.toBukkitScoreboard()).setAllowFriendlyFire(false);
    }

}