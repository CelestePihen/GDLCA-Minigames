package fr.cel.valocraft.manager;

import fr.cel.gameapi.scoreboard.GameTeam;
import org.bukkit.Location;
import org.bukkit.scoreboard.Team.OptionStatus;

public record Role(String name, String displayName, Location spawn, GameTeam team) {

    public Role(String name, String displayName, Location spawn, GameTeam team) {
        this.name = name;
        this.displayName = displayName;
        this.spawn = spawn;
        this.team = team;

        team.setNameTagVisibility(OptionStatus.FOR_OTHER_TEAMS);
        team.setAllowFriendlyFire(false);
    }

}