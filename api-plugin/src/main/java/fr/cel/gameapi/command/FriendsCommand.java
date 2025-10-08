package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.FriendsInventory;
import fr.cel.gameapi.manager.database.FriendsManager;
import fr.cel.gameapi.manager.database.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsCommand extends AbstractCommand {

    private final Map<Player, Player> requestsFriends = new HashMap<>();
    private final FriendsManager friendsManager;

    public FriendsCommand(FriendsManager friendsManager) {
        super("gameapi:friends", true, false);
        this.friendsManager = friendsManager;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return;
        }

        PlayerData playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player);
        if (playerData == null) {
            sendMessageWithPrefix(player, Component.text("Erreur avec votre profil ! Merci de vous déconnecter et vous reconnecter. Si le problème persiste, contactez un administrateur.", NamedTextColor.RED));
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept")) {
                if (!(requestsFriends.containsKey(player))) {
                    sendMessageWithPrefix(player, Component.text("Tu n'as pas de demandes d'ami en cours."));
                    return;
                }

                if (requestsFriends.get(player) == null) {
                    sendMessageWithPrefix(player, Component.text("Erreur lors de la création de la requête."));
                    return;
                }

                friendsManager.addFriend(requestsFriends.get(player), player);
                friendsManager.addFriend(player, requestsFriends.get(player));

                sendMessageWithPrefix(player, Component.text("Tu es désormais ami avec " + requestsFriends.get(player).getName() + "."));
                sendMessageWithPrefix(requestsFriends.get(player), Component.text("Tu es désormais ami avec " + player.getName() + "."));

                requestsFriends.remove(player);
                return;
            }

            else if (args[0].equalsIgnoreCase("deny")) {
                if (!(requestsFriends.containsKey(player))) {
                    sendMessageWithPrefix(player, Component.text("Tu n'as pas de demandes d'ami en cours."));
                    return;
                }

                if (requestsFriends.get(player) == null) {
                    sendMessageWithPrefix(player, Component.text("Erreur lors de la création de la requête."));
                    return;
                }

                sendMessageWithPrefix(player, Component.text("Tu as refusé la demande d'ami de " + requestsFriends.get(player).getName() + "."));
                sendMessageWithPrefix(requestsFriends.get(player), Component.text(player.getName() + "a refusé votre demande d'ami..."));

                requestsFriends.remove(player);
                return;
            }

            else if (args[0].equalsIgnoreCase("list")) {
                GameAPI.getInstance().getInventoryManager().openInventory(new FriendsInventory(player), player);
                return;
            }

            else if (args[0].equalsIgnoreCase("status")) {
                boolean allowFriends = playerData.isAllowingFriends();
                playerData.setAllowFriends(!allowFriends);
                sendMessageWithPrefix(player, allowFriends ? Component.text("Tu as désactivé les demandes d'amis.") : Component.text("Tu as activé les demandes d'amis."));
                return;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (args[1].equals(player.getName())) {
                    sendMessageWithPrefix(player, Component.text("Tu ne peux pas vous ajouter en ami..."));
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (isPlayerOnline(target, player)) {
                    if (friendsManager.isFriendWith(player, target)) {
                        sendMessageWithPrefix(player, Component.text("Tu es déjà ami avec cette personne."));
                        return;
                    }

                    if (requestsFriends.containsValue(player)) {
                        sendMessageWithPrefix(player, Component.text("Tu as déjà une demande d'ami en cours."));
                        return;
                    }

                    PlayerData friendData = GameAPI.getInstance().getPlayerManager().getPlayerData(target);
                    if (friendData == null) {
                        sendMessageWithPrefix(player, Component.text("Erreur avec le profil du joueur ! Si le problème persiste, contactez un administrateur.", NamedTextColor.RED));
                        return;
                    }

                    if (!playerData.isAllowingFriends()) {
                        sendMessageWithPrefix(player, Component.text("Tu n'acceptes pas les demandes d'amis."));
                    }

                    if (!friendData.isAllowingFriends()) {
                        sendMessageWithPrefix(target, Component.text(args[1] + " n'accepte pas les demandes d'amis."));
                    }

                    requestsFriends.put(target, player);
                    sendMessageWithPrefix(player, Component.text("Tu as demandé en ami " + args[1]));
                    sendMessageWithPrefix(target, Component.text(player.getName() + " vous a demandé en ami."));
                }
            }

            else if (args[0].equalsIgnoreCase("remove")) {
                if (args[1].equals(player.getName())) {
                    sendMessageWithPrefix(player, Component.text("Tu ne peux pas vous retirer..."));
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (isPlayerOnline(target, player)) {
                    if (!friendsManager.isFriendWith(player, target)) {
                        sendMessageWithPrefix(player, Component.text("Tu n'es pas ami avec cette personne."));
                        return;
                    }

                    sendMessageWithPrefix(player, Component.text("Tu n'es désormais plus ami avec " + target.getName()));
                    friendsManager.removeFriend(player, target);
                    friendsManager.removeFriend(target, player);
                }
            }

        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("add", "remove", "list", "accept", "deny", "status");
        }

        return null;
    }

    private void sendHelp(Player player) {
        player.sendMessage(" ");
        player.sendMessage(Component.text("[Aide pour les commandes du sytème d'amis]", NamedTextColor.GOLD));
        player.sendMessage("/friend add <pseudo> : Ajout d'un ami");
        player.sendMessage("/friend remove <pseudo> : Suppression d'un ami");
        player.sendMessage("/friend list : Vous envoie votre liste d'amis");
        player.sendMessage("/friend accept : Accepte la demande d'ami qui vous a été envoyé");
        player.sendMessage("/friend deny : Décline la demande d'ami qui vous a été envoyé");
        player.sendMessage("/friend status : Change votre status sur les demandes d'amis");
    }

}