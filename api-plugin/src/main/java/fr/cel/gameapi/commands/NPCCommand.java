package fr.cel.gameapi.commands;

import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.gameapi.manager.npc.NPCManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCCommand extends AbstractCommand {

    private final Map<JavaPlugin, NPCManager> npcsPlugin = new HashMap<>();

    public NPCCommand() {
        super("gameapi:npc", false, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
        }

        if (args.length == 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
            for (NPCManager npcManager : npcsPlugin.values()) npcManager.reloadNPCs();
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "rl");
        }
        return List.of();
    }

    public void addPlugin(JavaPlugin plugin, NPCManager npcManager) {
        this.npcsPlugin.put(plugin, npcManager);
    }

    public Collection<NPCManager> getNPCManagers() {
        return npcsPlugin.values();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(Component.text("[Aide pour les commandes de PNJ]", NamedTextColor.GOLD));
        sender.sendMessage("/npc reload|rl : Recharge tous les PNJ de tous les plugins");
    }

}