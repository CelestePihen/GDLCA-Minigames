package fr.cel.valocraft.arena.state.game;

import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.ArenaState;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.arena.state.provider.game.WaitingListenerProvider;
import fr.cel.valocraft.arena.timer.game.WaitingArenaTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

@Getter
public class WaitingArenaState extends ArenaState {

    private WaitingArenaTask waitingArenaTask;

    public WaitingArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        if (arena.getGlobalRound() == 1) addPlayersToBossBar();

        if (arena.getGlobalRound() == 13) arena.inverseTeam();

        removeSpike();
        arena.clearPlayers();
        arena.setGameModePlayers(GameMode.SURVIVAL);
        arena.showTeamRound();
        teleportPlayersToSpawnTeam();
        giveWeapons();
        showGlobalRound();

        int timer = (arena.getGlobalRound() == 1 || arena.getGlobalRound() == 13 || (arena.getBlueTeam().getRoundWin() == 12 && arena.getRedTeam().getRoundWin() == 12)) ? 45 : 30;
        waitingArenaTask = new WaitingArenaTask(arena, timer);
        waitingArenaTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (waitingArenaTask != null) waitingArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new WaitingListenerProvider(arena);
    }

    private void teleportPlayersToSpawnTeam() {
        for (String playerName : arena.getAttackers().getTeam().getPlayers()) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) player.teleport(arena.getAttackersSpawn());
        }

        for (String playerName : arena.getDefenders().getTeam().getPlayers()) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) player.teleport(arena.getDefendersSpawn());
        }
    }

    private void giveWeapons() {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;

            ItemStack bow = new ItemBuilder(Material.BOW).addEnchant(Enchantment.INFINITY, 1).toItemStack();
            ItemStack crossBow = new ItemBuilder(Material.CROSSBOW).toItemStack();
            ItemStack arrow = new ItemBuilder(Material.ARROW, 64).toItemStack();
                
            player.getInventory().addItem(bow, crossBow);
            player.getInventory().setItem(16, arrow);
        }

        Optional<String> player = arena.getAttackers().getTeam().getPlayers().stream().findFirst();
        if (player.isPresent()) {
            Player firstAttacker = Bukkit.getPlayer(player.get());
            if (firstAttacker != null) firstAttacker.getInventory().addItem(new ItemBuilder(Material.BREWING_STAND).setItemName("Spike").toItemStack());
        }
    }

    private void showGlobalRound() {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.sendTitle("Manche " + arena.getGlobalRound(), "", 10, 70, 20);
        }
    }

    private void addPlayersToBossBar() {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) arena.getBossBar().addPlayer(player);
        }
    }

    private void removeSpike() {
        if (arena.getSpike() != null) {
            arena.getSpike().setType(Material.AIR);
            arena.setSpike(null);
        }
    }

}