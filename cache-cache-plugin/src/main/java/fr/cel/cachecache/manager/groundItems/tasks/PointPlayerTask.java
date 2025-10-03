package fr.cel.cachecache.manager.groundItems.tasks;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PointPlayerTask extends BukkitRunnable {

    private final Player player;
    private final Player target;

    private int timer = 200;

    public PointPlayerTask(Player player, Player target) {
        this.player = player;
        this.target = target;
    }

    @Override
    public void run() {
        if (timer > 0) {
            player.sendActionBar(Component.text(getArrowDirection(player, target)));
            timer--;
        } else {
            cancel();
        }
    }

    /**
     * Retourne une flèche vers la direction du joueur
     * @param player L'instance du joueur
     * @param targetLoc La position du joueur visé
     * @return Retourne la flèche vers la direction
     * @author Deadsky
     */
    private String getArrowDirection(Player player, Player target) {
        String arrow = "?";

        Vector a = target.getLocation().toVector().setY(0).subtract(player.getLocation().toVector().setY(0));
        Vector b = player.getLocation().getDirection().setY(0);

        double angleDir = (Math.atan2(a.getZ(), a.getX()) / 2 / Math.PI * 360 + 360) % 360, angleLook = (Math.atan2(b.getZ(), b.getX()) / 2 / Math.PI * 360 + 360) % 360, angle = (angleDir - angleLook + 360) % 360;

        if (angle <= 022.5 || angle > 337.5) arrow = "⬆";
        else if (angle <= 337.5 && angle > 292.5) arrow = "⬆";
        else if (angle <= 292.5 && angle > 247.5) arrow = "←";
        else if (angle <= 347.5 && angle > 202.0) arrow = "⬋";
        else if (angle <= 202.0 && angle > 157.5) arrow = "⬇";
        else if (angle <= 157.5 && angle > 112.5) arrow = "⬊";
        else if (angle <= 112.5 && angle > 067.5) arrow = "→";
        else if (angle <= 067.5) arrow = "⬈";

        return arrow;
    }

}