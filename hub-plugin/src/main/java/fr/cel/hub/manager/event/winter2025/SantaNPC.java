package fr.cel.hub.manager.event.winter2025;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.npc.NPC;
import fr.cel.gameapi.manager.npc.Skin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.profile.PlayerTextures;

public class SantaNPC extends NPC {

    // TODO: change location when map will be finished
    public SantaNPC() {
        super(
                "Pere_Noel",
                Component.text("Père Noël", NamedTextColor.RED),
                new Location(Bukkit.getWorlds().getFirst(), 333.5, 115, 106.5, 90F, 0F),
                Pose.STANDING,
                new Skin(Key.key("gdlca", "entity/npc/santa"), null, null, PlayerTextures.SkinModel.CLASSIC));
    }

    @Override
    public void interact(Player player) {
        this.chat(Component.text("Ho Ho Ho ! Joyeux Noël ").append(Component.text(player.getName(), NamedTextColor.AQUA)).append(Component.text(" !")), player);
        GameAPI.getInstance().getInventoryManager().openInventory(new SantaInventory(player), player);
    }

}