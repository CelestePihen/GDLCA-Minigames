package fr.cel.valocraft.manager;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ValoTeam {
    
    private final String name;
    private final String displayName;
    private final Set<UUID> players;
    private Role role;
    @Setter private int roundWin;

    public ValoTeam(String name, String displayName, Role role) {
        this.name = name;
        this.displayName = displayName;
        this.players = new HashSet<>();
        this.role = role;
        this.roundWin = 0;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player.getUniqueId())) {
            role.getTeam().addPlayer(player);
            players.add(player.getUniqueId());
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player.getUniqueId())) {
            role.getTeam().removePlayer(player);
            players.remove(player.getUniqueId());
        }
    }

    public void clearPlayers() {
        players.forEach(uuid -> role.getTeam().removeEntity(uuid));
        players.clear();
    }

    public void setRole(Role role) {
        players.forEach(uuid -> this.role.getTeam().removeEntity(uuid));
        this.role = role;
        players.forEach(uuid -> this.role.getTeam().addEntity(uuid));
    }

    public boolean isAllTeamInSpec() {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (player.getGameMode() != GameMode.SPECTATOR) {
                return false;
            }

        }
        return true;
    }

}