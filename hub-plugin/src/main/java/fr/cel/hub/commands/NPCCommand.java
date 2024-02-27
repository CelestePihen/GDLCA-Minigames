package fr.cel.hub.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.hub.Hub;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommand extends AbstractCommand {

    public NPCCommand(Hub main) {
        super("hub:npc", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            sendMessageWithPrefix(player, "La commande est désactivé pour le moment.");
            sendMessageWithPrefix(player, "La commande est : /npc <reload>");
            return;
        }

        // TODO désactiver pour le moment
        /* if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            ((Hub)main).getNpcManager().reloadNPCs();
        }

        TODO À voir pour faire apparaître avec une commande
        if (args.length == 2) {
            if (args[0].length() >= 16) {
                sendMessageWithPrefix(player, "Le nom doit être inférieur à 16 caractères.");
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sendMessageWithPrefix(player, "Ce joueur n'est pas en ligne ou n'existe pas.");
                return;
            }

            Property property = (Property) ((CraftPlayer) target).getHandle().getGameProfile().getProperties().get("textures").toArray()[0];
            String texture = property.getValue();
            String signature = property.getSignature();

            NPC npc = new NPC(args[0], player.getLocation(), texture, signature);
            npc.create();
            npc.showToAll();

            main.getNpcManager().getNpcs().add(npc);
            sendMessageWithPrefix(player, "Tu as fait apparaître un nouveau PNJ " + args[0]);
        } */

    }

}