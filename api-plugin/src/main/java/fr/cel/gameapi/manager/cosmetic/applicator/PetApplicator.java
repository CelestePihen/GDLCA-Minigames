package fr.cel.gameapi.manager.cosmetic.applicator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.cosmetic.Cosmetic;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Applicator for pet cosmetics (companion entities that follow the player)
 */
public class PetApplicator implements CosmeticApplicator {

    private final Map<UUID, PetData> activePets = new HashMap<>();

    @Override
    public void apply(Player player, Cosmetic cosmetic) {
        remove(player, cosmetic);

        PetConfig config = parsePetConfig(cosmetic.getData());

        Location spawnLoc = player.getLocation().add(0, 0, -2);
        LivingEntity pet = (LivingEntity) player.getWorld().spawnEntity(spawnLoc, config.entityType);

        pet.customName(Component.text(cosmetic.getName()));
        pet.setCustomNameVisible(true);
        pet.setAI(false);
        pet.setInvulnerable(true);
        pet.setGravity(true);
        pet.setSilent(true);
        pet.setPersistent(false);

        // Make the pet follow the player
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(GameAPI.getInstance(), () -> {
            if (!player.isOnline() || player.isDead() || !pet.isValid()) {
                remove(player, cosmetic);
                return;
            }

            Location playerLoc = player.getLocation();
            Location petLoc = pet.getLocation();

            double distance = playerLoc.distance(petLoc);

            // Teleport if too far
            if (distance > 10) {
                Location teleportLoc = playerLoc.add(playerLoc.getDirection().multiply(-2));
                pet.teleport(teleportLoc);
            } else if (distance > 3) {
                // Move towards player
                Location targetLoc = playerLoc.add(playerLoc.getDirection().multiply(-2));
                targetLoc.setY(playerLoc.getY());

                double speed = Math.min(distance * 0.15, 0.5);
                Vector direction = targetLoc.toVector().subtract(petLoc.toVector()).normalize().multiply(speed);
                pet.setVelocity(direction);

                // Make pet look at player
                Vector directionToPlayer = playerLoc.toVector().subtract(petLoc.toVector());
                Location lookAt = petLoc.clone();
                lookAt.setDirection(directionToPlayer);
                pet.teleport(new Location(petLoc.getWorld(), petLoc.getX(), petLoc.getY(), petLoc.getZ(),
                        lookAt.getYaw(), lookAt.getPitch()));
            }
        }, 0L, 5L);

        activePets.put(player.getUniqueId(), new PetData(pet, task));
    }

    @Override
    public void remove(Player player, Cosmetic cosmetic) {
        PetData petData = activePets.remove(player.getUniqueId());
        if (petData != null) {
            petData.task.cancel();
            petData.entity.remove();
        }
    }

    @Override
    public void apply(Mannequin mannequin, Cosmetic cosmetic) {
        remove(mannequin, cosmetic);

        PetConfig config = parsePetConfig(cosmetic.getData());

        Location spawnLoc = mannequin.getLocation().add(0, 0, -2);
        LivingEntity pet = (LivingEntity) mannequin.getWorld().spawnEntity(spawnLoc, config.entityType);

        pet.customName(Component.text(cosmetic.getName()));
        pet.setCustomNameVisible(true);
        pet.setAI(false);
        pet.setInvulnerable(true);
        pet.setGravity(true);
        pet.setSilent(true);
        pet.setPersistent(false);

        // Make the pet follow the mannequin
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(GameAPI.getInstance(), () -> {
            if (mannequin.isDead() || !pet.isValid()) {
                remove(mannequin, cosmetic);
                return;
            }

            Location mannequinLoc = mannequin.getLocation();
            Location petLoc = pet.getLocation();

            double distance = mannequinLoc.distance(petLoc);

            // Teleport if too far
            if (distance > 10) {
                Location teleportLoc = mannequinLoc.add(mannequinLoc.getDirection().multiply(-2));
                pet.teleport(teleportLoc);
            } else if (distance > 3) {
                // Move towards mannequin
                Location targetLoc = mannequinLoc.add(mannequinLoc.getDirection().multiply(-2));
                targetLoc.setY(mannequinLoc.getY());

                double speed = Math.min(distance * 0.15, 0.5);
                Vector direction = targetLoc.toVector().subtract(petLoc.toVector()).normalize().multiply(speed);
                pet.setVelocity(direction);

                // Make pet look at mannequin
                Vector directionToMannequin = mannequinLoc.toVector().subtract(petLoc.toVector());
                Location lookAt = petLoc.clone();
                lookAt.setDirection(directionToMannequin);
                pet.teleport(new Location(petLoc.getWorld(), petLoc.getX(), petLoc.getY(), petLoc.getZ(),
                        lookAt.getYaw(), lookAt.getPitch()));
            }
        }, 0L, 5L);

        activePets.put(mannequin.getUniqueId(), new PetData(pet, task));
    }

    @Override
    public void remove(Mannequin mannequin, Cosmetic cosmetic) {
        PetData petData = activePets.remove(mannequin.getUniqueId());
        if (petData != null) {
            petData.task.cancel();
            petData.entity.remove();
        }
    }

    /**
     * Parse pet configuration from cosmetic data
     * @param data The cosmetic data JSON
     * @return The pet configuration
     */
    private PetConfig parsePetConfig(String data) {
        try {
            JsonObject json = JsonParser.parseString(data).getAsJsonObject();

            EntityType entityType = EntityType.valueOf(
                json.has("entityType") ? json.get("entityType").getAsString() : "WOLF"
            );

            return new PetConfig(entityType);
        } catch (Exception e) {
            return new PetConfig(EntityType.WOLF);
        }
    }

    /**
     * Remove all active pets
     */
    public void removeAll() {
        activePets.values().forEach(petData -> {
            petData.task.cancel();
            petData.entity.remove();
        });
        activePets.clear();
    }

    private record PetData(LivingEntity entity, BukkitTask task) { }

    private record PetConfig(EntityType entityType) { }
}

