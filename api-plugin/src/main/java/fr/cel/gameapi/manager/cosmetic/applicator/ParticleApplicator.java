package fr.cel.gameapi.manager.cosmetic.applicator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Applicator for particle cosmetics
 */
public class ParticleApplicator implements CosmeticApplicator {

    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    @Override
    public void apply(Player player, Cosmetic cosmetic) {
        // Cancel any existing particle effect
        remove(player, cosmetic);

        ParticleEffect effect = parseParticleEffect(cosmetic.getData());

        // Display particles
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(GameAPI.getInstance(), () -> {
            if (!player.isOnline() || player.isDead()) {
                remove(player, cosmetic);
                return;
            }

            Location loc = player.getLocation().add(0, 1, 0);
            player.getWorld().spawnParticle(
                effect.particle,
                loc,
                effect.count,
                effect.offsetX,
                effect.offsetY,
                effect.offsetZ,
                effect.speed
            );
        }, 0L, effect.interval);

        activeTasks.put(player.getUniqueId(), task);
    }

    @Override
    public void remove(Player player, Cosmetic cosmetic) {
        BukkitTask task = activeTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }

    @Override
    public void apply(Mannequin mannequin, Cosmetic cosmetic) {
        // Cancel any existing particle effect
        remove(mannequin, cosmetic);

        ParticleEffect effect = parseParticleEffect(cosmetic.getData());

        // Display particles
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(GameAPI.getInstance(), () -> {
            if (mannequin.isDead()) {
                remove(mannequin, cosmetic);
                return;
            }

            Location loc = mannequin.getLocation().add(0, 1, 0);
            mannequin.getWorld().spawnParticle(
                    effect.particle,
                    loc,
                    effect.count,
                    effect.offsetX,
                    effect.offsetY,
                    effect.offsetZ,
                    effect.speed
            );
        }, 0L, effect.interval);

        activeTasks.put(mannequin.getUniqueId(), task);
    }

    @Override
    public void remove(Mannequin mannequin, Cosmetic cosmetic) {
        BukkitTask task = activeTasks.remove(mannequin.getUniqueId());
        if (task != null) task.cancel();
    }

    /**
     * Parse particle effect from cosmetic data
     * @param data The cosmetic data JSON
     * @return The particle effect configuration
     */
    private ParticleEffect parseParticleEffect(String data) {
        try {
            JsonObject json = JsonParser.parseString(data).getAsJsonObject();

            Particle particle = Particle.valueOf(
                json.has("particle") ? json.get("particle").getAsString() : "HEART"
            );
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            double offsetX = json.has("offsetX") ? json.get("offsetX").getAsDouble() : 0.5;
            double offsetY = json.has("offsetY") ? json.get("offsetY").getAsDouble() : 0.5;
            double offsetZ = json.has("offsetZ") ? json.get("offsetZ").getAsDouble() : 0.5;
            double speed = json.has("speed") ? json.get("speed").getAsDouble() : 0.0;
            long interval = json.has("interval") ? json.get("interval").getAsLong() : 10L;

            return new ParticleEffect(particle, count, offsetX, offsetY, offsetZ, speed, interval);
        } catch (Exception e) {
            return new ParticleEffect(Particle.HEART, 1, 0.5, 0.5, 0.5, 0.0, 10L);
        }
    }

    /**
     * Stop all active particle effects
     */
    public void removeAll() {
        activeTasks.values().forEach(BukkitTask::cancel);
        activeTasks.clear();
    }

    private record ParticleEffect(Particle particle, int count, double offsetX, double offsetY, double offsetZ,
                                  double speed, long interval) {}
}

