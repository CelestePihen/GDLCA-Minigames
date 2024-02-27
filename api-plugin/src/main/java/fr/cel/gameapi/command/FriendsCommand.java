package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.FriendsManager;
import fr.cel.gameapi.utils.PlayerData;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class FriendsCommand extends AbstractCommand {

    public Map<Player, Player> requestsFriends = new HashMap<>();
    private final FriendsManager friendsManager = GameAPI.getInstance().getFriendsManager();

    public FriendsCommand() {
        super("gameapi:friends", true, false);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            sendHelp(player);
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept")) {
                if (!(requestsFriends.containsKey(player))) {
                    sendMessageWithPrefix(player, "Vous n'avez pas de demandes d'ami en cours.");
                    return;
                }

                if (requestsFriends.get(player) == null) {
                    sendMessageWithPrefix(player, "Erreur lors de la création de la requête.");
                    return;
                }

                if (friendsManager.isFriendWith(player, requestsFriends.get(player))) {
                    sendMessageWithPrefix(player, "Vous êtes déjà ami avec cette personne.");
                    return;
                }

                friendsManager.addFriend(requestsFriends.get(player), player);
                friendsManager.addFriend(player, requestsFriends.get(player));

                sendMessageWithPrefix(player, "Vous êtes désormais ami avec " + requestsFriends.get(player).getName() + ".");
                sendMessageWithPrefix(requestsFriends.get(player), "Vous êtes désormais ami avec " + player.getName() + ".");

                requestsFriends.remove(player);
                return;
            }

            else if (args[0].equalsIgnoreCase("deny")) {
                if (!(requestsFriends.containsKey(player))) {
                    sendMessageWithPrefix(player, "Vous n'avez pas de demandes d'ami en cours.");
                    return;
                }

                if (requestsFriends.get(player) == null) {
                    sendMessageWithPrefix(player, "Erreur lors de la création de la requête.");
                    return;
                }

                if (friendsManager.isFriendWith(player, requestsFriends.get(player))) {
                    sendMessageWithPrefix(player, "Vous êtes déjà ami avec cette personne.");
                    return;
                }

                sendMessageWithPrefix(player, "Vous avez refusé la demande d'ami de " + requestsFriends.get(player).getName() + ".");
                sendMessageWithPrefix(requestsFriends.get(player), player.getName() + "a refusé votre demande d'ami...");

                requestsFriends.remove(player);
                return;
            }

            else if (args[0].equalsIgnoreCase("list")) {
                if (friendsManager.getFriendCounter(player) == 0) {
                    sendMessageWithPrefix(player, "Vous n'avez pas d'amis...");
                    return;
                }

                List<String> friendsOnline = new ArrayList<>();
                List<String> friendsOffline = new ArrayList<>();

                for (String friend : friendsManager.getFriendsList(player)) {
                    if (Bukkit.getPlayer(friend) == null) {
                        friendsOffline.add(friend);
                    } else {
                        friendsOnline.add(friend);
                    }
                }

                if (friendsOnline.isEmpty()) {
                    sendMessageWithPrefix(player, "Amis en ligne : Aucun(e) ami(e) en ligne.");
                } else {
                    StringBuilder message = new StringBuilder();
                    for (String s : friendsOnline) message.append(s).append(", ");
                    message.toString().trim();
                    sendMessageWithPrefix(player, "Amis en ligne : [" + message.substring(0, message.length() - 2) + "]");
                }

                if (friendsOffline.isEmpty()) {
                    sendMessageWithPrefix(player, "Amis en ligne : Aucun(e) ami(e) hors-ligne.");
                } else {
                    StringBuilder message = new StringBuilder();
                    for (String s : friendsOffline) message.append(s).append(", ");
                    message.toString().trim();
                    sendMessageWithPrefix(player, "Amis hors-ligne : [" + message.substring(0, message.length() - 2) + "]");
                }
                return;
            }

            else if (args[0].equalsIgnoreCase("status")) {
                PlayerData playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player);
                if (!playerData.isAllowingFriends()) {
                    playerData.setAllowFriends(true);
                    sendMessageWithPrefix(player, "Vous avez activé les demandes d'amis.");
                } else {
                    playerData.setAllowFriends(false);
                    sendMessageWithPrefix(player, "Vous avez désactivé les demandes d'amis.");
                }
                return;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                PlayerData playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player);
                Player target = Bukkit.getPlayer(args[1]);

                if (target != null) {
                    PlayerData friendData = GameAPI.getInstance().getPlayerManager().getPlayerData(target);
                    if (playerData.isAllowingFriends()) {
                        if (friendData.isAllowingFriends()) {
                            if (requestsFriends.containsValue(player)) {
                                sendMessageWithPrefix(player, "Vous avez déjà une demande d'ami en cours.");
                                return;
                            }

                            if (target == player) {
                                sendMessageWithPrefix(player, "Vous ne pouvez pas vous ajouter en ami...");
                                return;
                            }

                            if (friendsManager.isFriendWith(player, target)) {
                                sendMessageWithPrefix(player, "Vous êtes déjà ami avec cette personne.");
                                return;
                            }

                            requestsFriends.put(target, player);
                            sendMessageWithPrefix(player, "Vous avez demandé en ami " + args[1]);
                            sendMessageWithPrefix(target, player.getName() + " vous a demandé en ami.");
                        } else {
                            sendMessageWithPrefix(target, args[1] + " n'accepte pas les demandes d'amis.");
                        }
                    } else {
                        sendMessageWithPrefix(player, "Vous n'acceptez pas les demandes d'amis.");
                    }
                } else {
                    sendMessageWithPrefix(player, "Ce joueur n'existe pas ou n'est pas en ligne.");
                }
            }

            else if (args[0].equalsIgnoreCase("remove")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target != null) {
                    if (!friendsManager.isFriendWith(player, target)) {
                        sendMessageWithPrefix(player, "Vous n'êtes pas ami avec cette personne.");
                        return;
                    }

                    sendMessageWithPrefix(player, "Vous n'êtes désormais plus ami avec " + target.getName());
                    friendsManager.removeFriend(player, target);
                    friendsManager.removeFriend(target, player);
                }
            }

        }

    }

    private void sendHelp(Player player) {
        player.sendMessage(" ");
        player.sendMessage(ChatUtility.format("[Aide pour les commandes du sytème d'amis]", ChatUtility.UtilityColor.GOLD));
        player.sendMessage("/friend add <pseudo> : Ajout d'un ami");
        player.sendMessage("/friend remove <pseudo> : Suppression d'un ami");
        player.sendMessage("/friend list : Vous envoie votre liste d'amis");
        player.sendMessage("/friend accept : Accepte la demande d'ami qui vous a été envoyé");
        player.sendMessage("/friend deny : Décline la demande d'ami qui vous a été envoyé");
        player.sendMessage("/friend status : Change votre status sur les demandes d'amis");
    }

}