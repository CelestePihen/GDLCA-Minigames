package fr.cel.hub.commands;

import com.destroystokyo.paper.profile.ProfileProperty;
import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.hub.Hub;
import fr.cel.hub.manager.event.winter2025.HeadManager;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CalculHeadsCommand extends AbstractCommand {

    private final Hub main;

    public CalculHeadsCommand(Hub main) {
        super("hub:calculheads", true, true);
        this.main = main;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            sendMessageWithPrefix(player, Component.text("Le nombre d'emplacements des têtes est "
                    + main.getHeadManager().getHeadLocations().size() + ".", NamedTextColor.GREEN));
            return;
        }

        if (args.length != 6) {
            player.sendMessage(Hub.getPrefix().append(Component.text("Usage: /calculheads <x1> <y1> <z1> <x2> <y2> <z2>", NamedTextColor.RED)));
            return;
        }

        int x1, y1, z1, x2, y2, z2;
        try {
            x1 = Integer.parseInt(args[0]);
            y1 = Integer.parseInt(args[1]);
            z1 = Integer.parseInt(args[2]);
            x2 = Integer.parseInt(args[3]);
            y2 = Integer.parseInt(args[4]);
            z2 = Integer.parseInt(args[5]);
        } catch (NumberFormatException ignored) {
            player.sendMessage(Hub.getPrefix().append(Component.text("Merci de rentrer des entiers valides.", NamedTextColor.RED)));
            return;
        }

        // Assurer que x1 <= x2, y1 <= y2, z1 <= z2
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        World world = player.getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.PLAYER_HEAD) {
                        Skull skull = (Skull) block.getState();
                        ResolvableProfile profile = skull.getProfile();
                        if (profile == null) continue;

                        boolean isGiftHead = false;
                        for (ProfileProperty property : profile.properties()) {
                            if (property.getValue().equalsIgnoreCase(HeadManager.VALUE_GIFT_TEXTURE)) {
                                isGiftHead = true;
                                break;
                            }
                        }

                        if (isGiftHead) main.getHeadManager().addHead(block.getLocation(), main.getHeadManager().getNextAvailableId());
                    }
                }
            }
        }

        player.sendMessage(Hub.getPrefix().append(Component.text(main.getHeadManager().getHeadLocations().size() + " emplacements de cadeaux trouvés dans le Hub et sauvegardés !", NamedTextColor.GREEN)));
    }

    @Override
    protected List<String> onTabComplete(Player player, @NotNull String[] strings) {
        return List.of();
    }
}