package fr.cel.manhunt.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.cel.manhunt.ManHunt;
import fr.cel.manhunt.tasks.CompassTask;
import fr.cel.manhunt.utils.ItemBuilder;

public class HuntCommand extends AbstractCommand {

    private CompassTask compassTask;

    public HuntCommand(ManHunt main) {
        super(main);
    }

    @Override
    protected void onExecute(Player player, String[] args) {

        if (args[0].equalsIgnoreCase("boussole") || args[0].equalsIgnoreCase("compass")) {
            player.getInventory().addItem(new ItemBuilder(Material.COMPASS).toItemStack());
            return;
        }

        if (args[0].equalsIgnoreCase("addman")) {
            if (main.getMan().contains(player.getUniqueId())) return;
            main.addMan(player.getUniqueId());
            return;
        }

        if (args[0].equalsIgnoreCase("start")) {
            compassTask = new CompassTask(main);
            compassTask.runTaskTimer(main, 0, 20);
        }

        else {
            player.sendMessage(main.getPrefix() + "Vous devez mettre /manhunt <boussole/compass/addman/addhunter/start>");
        }

    }

}