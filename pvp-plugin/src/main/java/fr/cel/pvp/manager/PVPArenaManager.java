package fr.cel.pvp.manager;

import fr.cel.pvp.PVP;
import fr.cel.pvp.arena.ArenaConfig;
import fr.cel.pvp.arena.PVPArena;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PVPArenaManager {

    @Getter private static PVPArenaManager arenaManager;
    @Getter private final Map<String, PVPArena> arenas = new HashMap<>();
    private final PVP main;

    public PVPArenaManager(PVP main) {
        arenaManager = this;
        this.main = main;
        this.loadArenas();
    }

    public PVPArena getArenaByPlayer(Player player) {
        return arenas.values().stream().filter(pvpArena -> pvpArena.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
    }

    public PVPArena getArenaByDisplayName(String displayName) {
        return arenas.values().stream().filter(pvpArena -> pvpArena.getDisplayName().equals(displayName)).findFirst().orElse(null);
    }

    public boolean isPlayerInArena(Player player) {
        return arenas.values().stream().anyMatch(pvpArena -> pvpArena.isPlayerInArena(player));
    }

    public void loadArenas() {
        arenas.clear();

        File folder = new File(main.getDataFolder(), "arenas");
        if (!folder.exists()) folder.mkdirs();

        if (folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".yml")))) {
                String arenaName = file.getName().replace(".yml", "");
                ArenaConfig arenaConfig = new ArenaConfig(main, arenaName);
                PVPArena arena = arenaConfig.getArena();

                if (arena != null) arenas.put(arenaName, arena);
            }
        }

        Bukkit.getConsoleSender().sendMessage(GameManager.getPrefix().append(Component.text("Chargement de " + arenas.size() + " arènes PVP ", NamedTextColor.YELLOW)));
    }

}