package fr.cel.valocraft.manager;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public final class ValoTeam {
    
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
            role.team().addPlayer(player);
            players.add(player.getUniqueId());
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player.getUniqueId())) {
            role.team().removePlayer(player);
            players.remove(player.getUniqueId());
        }
    }

    public void resetTeam() {
        players.forEach(uuid -> role.team().removePlayer(Bukkit.getPlayer(uuid)));
        players.clear();

        roundWin = 0;
    }

    public void setRole(Role role) {
        players.forEach(uuid -> this.role.team().removePlayer(Bukkit.getPlayer(uuid)));
        this.role = role;
        players.forEach(uuid -> this.role.team().addPlayer(Bukkit.getPlayer(uuid)));
    }

    public boolean isAllTeamInSpec() {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (player.getGameMode() != GameMode.SPECTATOR) return false;
            }
        }
        return true;
    }

}