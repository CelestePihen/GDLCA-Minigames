package fr.cel.valocraft.arena.state.pregame;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.Role;
import fr.cel.valocraft.manager.ValoTeam;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.arena.state.provider.pregame.PreGameListenerProvider;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.ArenaState;
import org.bukkit.ChatColor;

public class PreGameArenaState extends ArenaState {

    public PreGameArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        arena.setAttackers(new Role("attackers", "Attaquants", arena.getAttackersSpawn(), arena.getScoreboard().registerTeam("a" + arena.getNameArena(), ChatColor.RED)));
        arena.setDefenders(new Role("defenders", "Défenseurs", arena.getDefendersSpawn(), arena.getScoreboard().registerTeam("d" + arena.getNameArena(), ChatColor.BLUE)));

        arena.setRedTeam(new ValoTeam("redTeam", "Équipe Rouge", arena.getAttackers()));
        arena.setBlueTeam(new ValoTeam("blueTeam", "Équipe Bleue", arena.getDefenders()));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new PreGameListenerProvider(arena);
    }

}