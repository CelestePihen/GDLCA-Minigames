package fr.cel.essentials;

import fr.cel.essentials.commands.other.*;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.CommandsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.essentials.commands.inventory.AnvilCommand;
import fr.cel.essentials.commands.inventory.CraftCommand;
import fr.cel.essentials.commands.inventory.EnderChestCommand;
import fr.cel.essentials.commands.utils.*;
import fr.cel.essentials.listener.EntityListener;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Essentials extends JavaPlugin {

    @Getter private final List<UUID> playersInGod = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new EntityListener(this), this);

        CommandsManager commandsManager = GameAPI.getInstance().getCommandsManager();
        commandsManager.addCommand(getCommand("heal"), new HealCommand());
        commandsManager.addCommand(getCommand("feed"), new FeedCommand());
        commandsManager.addCommand(getCommand("craft"), new CraftCommand());
        commandsManager.addCommand(getCommand("gm"), new GMCommand());
        commandsManager.addCommand(getCommand("top"), new TopCommand());
        commandsManager.addCommand(getCommand("nv"), new NightVisionCommand());
        commandsManager.addCommand(getCommand("bc"), new BroadcastCommand());
        commandsManager.addCommand(getCommand("speed"), new SpeedCommand());
        commandsManager.addCommand(getCommand("ec"), new EnderChestCommand());
        commandsManager.addCommand(getCommand("anvil"), new AnvilCommand());
        commandsManager.addCommand(getCommand("hat"), new HatCommand());
        commandsManager.addCommand(getCommand("near"), new NearCommand());
        commandsManager.addCommand(getCommand("god"), new GodCommand(this));
        commandsManager.addCommand(getCommand("discord"), new DiscordCommand());
        commandsManager.addCommand(getCommand("fly"), new FlyCommand());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public boolean containsPlayersInGod(Player player) {
        return playersInGod.contains(player.getUniqueId());
    }

}