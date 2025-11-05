package fr.cel.essentials;

import fr.cel.essentials.commands.general.*;
import fr.cel.essentials.commands.utils.*;
import fr.cel.essentials.listener.EntityListener;
import fr.cel.gameapi.manager.command.CommandsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Essentials extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        registerCommands();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void registerCommands() {
        CommandsManager commandsManager = new CommandsManager(this);
        commandsManager.addCommand("heal", new HealCommand());
        commandsManager.addCommand("feed", new FeedCommand());
        commandsManager.addCommand("gm", new GMCommand());
        commandsManager.addCommand("top", new TopCommand());
        commandsManager.addCommand("nv", new NightVisionCommand());
        commandsManager.addCommand("bc", new BroadcastCommand());
        commandsManager.addCommand("speed", new SpeedCommand());
        commandsManager.addCommand("hat", new HatCommand());
        commandsManager.addCommand("near", new NearCommand());
        commandsManager.addCommand("god", new GodCommand());
        commandsManager.addCommand("discord", new DiscordCommand());
        commandsManager.addCommand("fly", new FlyCommand());
        commandsManager.addCommand("barrier", new BarrierCommand());
    }

}