package fr.cel.valocraft.manager.arena.state.game;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.state.provider.game.WaitingListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.valocraft.manager.arena.state.ArenaState;
import fr.cel.valocraft.manager.arena.timer.game.WaitingArenaTask;
import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;

public class WaitingArenaState extends ArenaState {

    @Getter private WaitingArenaTask waitingArenaTask;

    public WaitingArenaState(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);

        if (getArena().getGlobalRound() == 1) addPlayersToBossBar();
        if (getArena().getGlobalRound() == 13) getArena().inverseTeam();

        getArena().clearPlayers();
        getArena().setGameModePlayers(GameMode.SURVIVAL);
        getArena().clearPlayers();
        getArena().showTeamRound();
        teleportPlayersToSpawnTeam();
        giveWeapons();
        showGlobalRound();
        removeSpike();

        int timer = (getArena().getGlobalRound() == 1 || getArena().getGlobalRound() == 13 || (getArena().getRoundWinBlue() == 12 && getArena().getRoundWinRed() == 12)) ? 45 : 30;
        waitingArenaTask = new WaitingArenaTask(getArena(), timer);
        waitingArenaTask.runTaskTimer(main, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (waitingArenaTask != null) waitingArenaTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new WaitingListenerProvider(getArena());
    }

    private void teleportPlayersToSpawnTeam() {
        for (UUID uuid : getArena().getAttackers().getTeam().getEntities()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.teleport(getArena().getAttackersSpawn());
        }

        for (UUID uuid : getArena().getDefenders().getTeam().getEntities()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.teleport(getArena().getDefendersSpawn());
        }
    }

    private void giveWeapons() {
        for (UUID uuid : getArena().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;

            ItemStack bow = new ItemBuilder(Material.BOW).setDisplayName("Arc").addEnchant(Enchantment.ARROW_INFINITE, 1).toItemStack();
            ItemStack crossBow = new ItemBuilder(Material.CROSSBOW).setDisplayName("Arbalète").toItemStack();
            ItemStack arrow = new ItemBuilder(Material.ARROW, 64).toItemStack();
                
            player.getInventory().addItem(bow, crossBow);
            player.getInventory().setItem(16, arrow);
        }
        Bukkit.getPlayer(getArena().getAttackers().getTeam().getEntities().get(0)).getInventory().addItem(new ItemBuilder(Material.BREWING_STAND).setDisplayName("Spike").toItemStack());
    }

    private void showGlobalRound() {
        for (UUID uuid : getArena().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.sendTitle("Manche " + getArena().getGlobalRound(), "", 10, 70, 20);
        }
    }

    private void addPlayersToBossBar() {
        for (UUID uuid : getArena().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            getArena().getBossBar().addPlayer(player);
        }
    }

    private void removeSpike() {
        if (getArena().getSpike() != null) {
            getArena().getSpike().setType(Material.AIR);
            getArena().setSpike(null);
        }
    }

}