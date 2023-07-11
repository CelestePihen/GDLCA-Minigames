package fr.cel.valocraft.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class ValoTeam {
    
    @Getter private String name;
    @Getter private String displayName;

    @Getter private List<UUID> players;

    @Getter @Setter private int roundWin;
    
    @Getter private Role role;

    public ValoTeam(String name, String displayName, Role role) {
        this.name = name;
        this.displayName = displayName;
        
        this.players = new ArrayList<>();
        
        this.roundWin = 0;
        this.role = role;
    }

    public void addPlayer(Player player) {
        if (containsPlayer(player)) return;

        role.getTeam().addPlayer(player);
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        if (containsPlayer(player)) {
            role.getTeam().removePlayer(player);
            players.remove(player.getUniqueId());
        }
    }

    public void clearPlayers() {
        players.clear();
        players.forEach(t -> { role.getTeam().removeEntity(t); });
    }

    public boolean containsPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void setRole(Role role) {
        players.forEach(uuid -> { this.role.getTeam().removeEntity(uuid); });

        this.role = role;

        players.forEach(uuid -> { this.role.getTeam().addEntity(uuid); });

    }

    public boolean isTeamInSpec() {
        for (UUID uuid : players) {
            if (Bukkit.getPlayer(uuid).getGameMode() != GameMode.SPECTATOR) return false;
        }
        return true;
    }

}