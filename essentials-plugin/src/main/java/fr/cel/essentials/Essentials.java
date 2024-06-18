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

    @Override
    public void onEnable() {
        super.onEnable();

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new EntityListener(), this);

        CommandsManager commandsManager = GameAPI.getInstance().getCommandsManager();
        commandsManager.addCommand("heal", new HealCommand(), this);
        commandsManager.addCommand("feed", new FeedCommand(), this);
        commandsManager.addCommand("craft", new CraftCommand(), this);
        commandsManager.addCommand("gm", new GMCommand(), this);
        commandsManager.addCommand("top", new TopCommand(), this);
        commandsManager.addCommand("nv", new NightVisionCommand(), this);
        commandsManager.addCommand("bc", new BroadcastCommand(), this);
        commandsManager.addCommand("speed", new SpeedCommand(), this);
        commandsManager.addCommand("ec", new EnderChestCommand(), this);
        commandsManager.addCommand("anvil", new AnvilCommand(), this);
        commandsManager.addCommand("hat", new HatCommand(), this);
        commandsManager.addCommand("near", new NearCommand(), this);
        commandsManager.addCommand("god", new GodCommand(), this);
        commandsManager.addCommand("discord", new DiscordCommand(), this);
        commandsManager.addCommand("fly", new FlyCommand(), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}