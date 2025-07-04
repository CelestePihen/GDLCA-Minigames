package fr.cel.gameapi.command;

import fr.cel.gameapi.manager.npc.NPCManager;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class NPCCommand extends AbstractCommand {

    private final Map<JavaPlugin, NPCManager> npcsPlugin = new HashMap<>();

    public NPCCommand() {
        super("gameapi:npc", false, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
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

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatUtility.format("[Aide pour les commandes de PNJ]", ChatUtility.GOLD));
        sender.sendMessage("/npc reload|rl : Recharge tous les PNJ de tous les plugins");
    }

}