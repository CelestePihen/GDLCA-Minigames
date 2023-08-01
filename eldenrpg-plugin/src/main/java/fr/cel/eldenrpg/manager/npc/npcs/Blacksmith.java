package fr.cel.eldenrpg.manager.npc.npcs;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.player.ERPlayer;
import fr.cel.eldenrpg.manager.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

public class Blacksmith extends NPC {

    public Blacksmith(EldenRPG main) {
        super("blacksmith", "ForgeronTest", new Location(Bukkit.getWorld("world"), 206.5, 65, -63.5),
                "ewogICJ0aW1lc3RhbXAiIDogMTY2Nzc2MTgwODM4OCwKICAicHJvZmlsZUlkIiA6ICIzZmFhZDFiMWYxMzU0MDM2OGY3YjlmMThlM2Y5YzRiZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJKb3NlUGxheTE5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQwMTEyMmI2MGMyYjk1ZWVlOTkwOWY3MzgwMjQ0NDNmMDk1M2NlMWNjZjUyZTIzM2FmMTdjZjk1ODlmMTI0MmMiCiAgICB9CiAgfQp9",
                "WwMc813z9ugUrcWte5oXgL3zMY3FGPNwk9U5Yb1e50elNjneCW4AmO9PYVr2TglDgu+p2wrPgIMM0lw9wEQpoW6OQu5ks6OMb/GGDml8v5vvTLjfLZ9GAzeIrHFNNCrsdBZMR7seFUzL/MkmLe9YS0OO7Og5Tjn5KJjfLN+zgmtVtX5N5iyt3fj7ql5iIOmUVR7xZ9TkheriBNRHsIdzttHyY8ZXs6e9HjCtJQIWSCzYkP9gnlP0kqjJehrYpy4pzdIhARIpDG8usMcuGv+Loj0411bpyCUdEHpO8wC5LEz3I7Q3YnojBzFudFokgZiZgYCU+l5FCIuqs1RukgkM1grKtXasmdp5mjgCLx1Sg2FJ331FUP/PmgKaGvOKk4yIcg74dVDRmZeNAsB2Gi7zTqWUwN2QFBSN5peJDXm2X7w3C7XpU0QULKJBBgGWKr3GeUja24GytKqWYkmefkAmqDP+EWR9dVhNpVjBHavvMkgqivRxCBTsEbAqMR0djyHYj1/roG/FiJfXk+52Uxa3OtXOHacIoQHZY+dON+7nSSB8RRviME21Wr728Lz3aPzpbcP6u/4pPUM07hvyRNJttUlj//9h2re5rqOSKmwURGeHhqRGKThgquWX4rJ1BVIsP2+nRjWeLfiXa29GZ+IGrIHOWPAaTNWzEbQmw3NQv1U=",
                main
        );
    }

    @Override
    public void interact(Player player, ERPlayer erPlayer) {

        if (erPlayer.hasActiveQuest(getQuest())) {
            sendMessageWithName(player, "Vous devez tuer les 4 slimes !");
        }

        else if (erPlayer.hasFinishedQuest(getQuest())) {
            erPlayer.finishedToCompleted(getQuest());
            sendMessageWithName(player, "Vous les avez tué, merci ! Voici votre récompense.");
            player.getInventory().addItem(new ItemStack(Material.DIAMOND));
        }

        else if (erPlayer.hasCompletedQuest(getQuest())) {
            sendMessageWithName(player, "Merci encore d'avoir tué ces slimes !");
        }

        else {
            erPlayer.addActiveQuest(getQuest());
            sendMessageWithName(player, "Bonjour aventurier. Pouvez-vous tuer ces 4 slimes dans ma maison. Vous aurez une récompense si vous le faites !");
            for (int i = 1; i < 5; i++) {
                Slime slime = (Slime) getLocation().getWorld().spawnEntity(new Location(Bukkit.getWorld("world"), 207.5, 69, -49.5), EntityType.SLIME);
                slime.setSize(1);
            }
        }

    }

}