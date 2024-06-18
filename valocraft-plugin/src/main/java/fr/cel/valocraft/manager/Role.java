package fr.cel.valocraft.manager;

import fr.cel.gameapi.scoreboard.GameTeam;
import org.bukkit.Location;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import lombok.Getter;

@Getter
public class Role {

    private final String name;
    private final String displayName;
    private final Location spawn;
    private final GameTeam team;

    public Role(String name, String displayName, Location spawn, GameTeam team) {
        this.name = name;
        this.displayName = displayName;
        this.spawn = spawn;
        this.team = team;

        team.setNameTagVisibility(OptionStatus.FOR_OTHER_TEAMS);
        team.setAllowFriendlyFire(false);
    }

}