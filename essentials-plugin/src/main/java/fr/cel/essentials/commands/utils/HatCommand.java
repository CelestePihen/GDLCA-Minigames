package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HatCommand extends AbstractCommand {

    public HatCommand() {
        super("essentials:hat", true, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        Player player = (Player) sender;

        ItemStack it = player.getInventory().getItemInMainHand().clone();
        it.setAmount(1);

        player.getInventory().setHelmet(it);

        String itemTranslationKey = it.getType().getItemTranslationKey();
        String blockTranslationKey = it.getType().getBlockTranslationKey();

        if (it.getType() == Material.AIR) {
            sendMessageWithPrefix(player, Component.text("Tu n'as plus rien sur ta tête."));
        } else if (itemTranslationKey != null) {
            sendMessageWithPrefix(player, Component.text("Tu as mis sur ta tête : ").append(Component.translatable(itemTranslationKey)));
        } else if (blockTranslationKey != null) {
            sendMessageWithPrefix(player, Component.text("Tu as mis sur ta tête : ").append(Component.translatable(blockTranslationKey)));
        } else {
            sendMessageWithPrefix(player, Component.text("Tu as mis sur ta tête : " + it.getType().name()));
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}