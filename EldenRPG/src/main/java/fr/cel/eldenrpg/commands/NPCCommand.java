package fr.cel.eldenrpg.commands;

import fr.cel.eldenrpg.EldenRPG;
import org.bukkit.entity.Player;

public class NPCCommand extends AbstractCommand {

    public NPCCommand(EldenRPG main) {
        super(main, "npc");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        if (args.length == 0) {
            sendMessageWithPrefix(player, "La commande est : /npc <reload>");
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            main.getNpcManager().reloadNPCs();
        }

    }

    @Override
    public void onTabComplete(Player player, String label, String[] args) {
        if (args.length == 1) {
            arguments.add("reload");
        }
    }

}