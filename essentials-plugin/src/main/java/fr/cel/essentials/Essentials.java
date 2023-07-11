package fr.cel.essentials;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.essentials.commands.inventory.AnvilCommand;
import fr.cel.essentials.commands.inventory.CraftCommand;
import fr.cel.essentials.commands.inventory.EnderChestCommand;
import fr.cel.essentials.commands.other.FeedCommand;
import fr.cel.essentials.commands.other.GMCommand;
import fr.cel.essentials.commands.other.GodCommand;
import fr.cel.essentials.commands.other.HealCommand;
import fr.cel.essentials.commands.other.SpeedCommand;
import fr.cel.essentials.commands.utils.*;
import fr.cel.essentials.listener.EntityListener;
import fr.cel.hub.utils.ChatUtility;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Essentials extends JavaPlugin {

    @Getter private List<UUID> playersInGod = new ArrayList<>();
    @Getter private String prefix = ChatUtility.format("&6[GDLCA Minigames] &f");

    @Override
    public void onEnable() {
        super.onEnable();

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new EntityListener(this), this);

        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("feed").setExecutor(new FeedCommand(this));
        getCommand("craft").setExecutor(new CraftCommand(this));
        getCommand("gm").setExecutor(new GMCommand(this));
        getCommand("top").setExecutor(new TopCommand(this));
        getCommand("nv").setExecutor(new NightVisionCommand(this));
        getCommand("bc").setExecutor(new BroadcastCommand(this));
        getCommand("speed").setExecutor(new SpeedCommand(this));
        getCommand("ec").setExecutor(new EnderChestCommand(this));
        getCommand("anvil").setExecutor(new AnvilCommand(this));
        getCommand("hat").setExecutor(new HatCommand(this));
        getCommand("near").setExecutor(new NearCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));
        getCommand("discord").setExecutor(new DiscordCommand(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public boolean containsPlayersInGod(Player player) {
        return playersInGod.contains(player.getUniqueId());
    }

}