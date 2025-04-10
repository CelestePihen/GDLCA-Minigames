package fr.cel.dailyquests.listener;

import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Condition;
import fr.cel.dailyquests.manager.quest.QuestData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class QuestListener implements Listener {

    private final QuestManager questManager;

    private final List<Material> ores = List.of(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.NETHER_QUARTZ_ORE, Material.GLOWSTONE,
            Material.NETHER_GOLD_ORE, Material.ANCIENT_DEBRIS);

    private final List<Material> crops = List.of(Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.SUGAR_CANE);

    private final List<Material> plants = List.of(Material.NETHER_WART,
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
            Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS,
            Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET,
            Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY,
            Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY, Material.CLOSED_EYEBLOSSOM, Material.OPEN_EYEBLOSSOM, Material.WITHER_ROSE,
            Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY, Material.SEA_PICKLE);

    private final List<Material> mushrooms = List.of(
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
            Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS);

    public QuestListener(QuestManager questManager) {
        this.questManager = questManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // quête journalière et hebdomadaire
        Player player = event.getPlayer();
        QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());
        if (qPlayer == null) return;

        Block block = event.getBlock();
        Material material = block.getType();

        // vérif pour quête journalière
        QuestData dailyQuestData = qPlayer.getDailyQuest();
        if (!dailyQuestData.isFinished()) {
            Material blockMined;
            try {
                blockMined = Material.valueOf(dailyQuestData.getQuest().getCondition().type());
                if (blockMined.isBlock() && blockMined == material) {
                    if (block.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
                            if (dailyQuestData.isFinished()) {
                                dailyQuestData.setFinished(player);
                            }
                        }
                    } else {
                        dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
                        if (dailyQuestData.isFinished()) {
                            dailyQuestData.setFinished(player);
                        }
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // on vérifie si le type de la condition est un matérial
                // si cela renvoie IllegalArgumentException alors ce n'est pas un matérial
            }
        }

        // vérif pour quête hebdomadaire
        QuestData weeklyQuestData = qPlayer.getWeeklyQuest();
        if (!weeklyQuestData.isFinished()) {
            // ANY
            if (weeklyQuestData.getQuest().getCondition().job() == Condition.Jobs.MINER
                    && weeklyQuestData.getQuest().getCondition().type().equalsIgnoreCase("ANY_ORE") && ores.contains(material)) {
                weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                if (weeklyQuestData.isFinished()) {
                    weeklyQuestData.setFinished(player);
                }
            }
            else if (weeklyQuestData.getQuest().getCondition().job() == Condition.Jobs.FARMER
                    && weeklyQuestData.getQuest().getCondition().type().equalsIgnoreCase("ANY_CROP") && crops.contains(material)
                    && block.getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {
                weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                if (weeklyQuestData.isFinished()) {
                    weeklyQuestData.setFinished(player);
                }
            }
            else if (weeklyQuestData.getQuest().getCondition().job() == Condition.Jobs.ALCHEMIST
                    && weeklyQuestData.getQuest().getCondition().type().equalsIgnoreCase("ANY_PLANT") && plants.contains(material)) {
                weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                if (weeklyQuestData.isFinished()) {
                    weeklyQuestData.setFinished(player);
                }
            }
            else if (weeklyQuestData.getQuest().getCondition().job() == Condition.Jobs.ALCHEMIST
                    && weeklyQuestData.getQuest().getCondition().type().equalsIgnoreCase("ANY_MUSHROOM") && mushrooms.contains(material)) {
                weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                if (weeklyQuestData.isFinished()) {
                    weeklyQuestData.setFinished(player);
                }
            }

            else {
                Material blockMined;
                try {
                    blockMined = Material.valueOf(weeklyQuestData.getQuest().getCondition().type());
                    if (blockMined.isBlock() && blockMined == material) {
                        if (block.getBlockData() instanceof Ageable ageable) {
                            if (ageable.getAge() == ageable.getMaximumAge()) {
                                dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
                                if (dailyQuestData.isFinished()) {
                                    dailyQuestData.setFinished(player);
                                }
                            }
                        } else {
                            if (weeklyQuestData.isFinished()) {
                                weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                            }
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                    // on vérifie si le type de la condition est un matérial
                    // si cela renvoie IllegalArgumentException alors ce n'est pas un matérial
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // quête journalière et hebdomadaire
        LivingEntity livingEntity = event.getEntity();

        Player player = livingEntity.getKiller();

        // si ce n'est pas un joueur qui a tué une Monstre alors on vérifie si c'est une flèche qui a été tirée par un joueur qui l'a tué
        if (player == null) {
            if (livingEntity instanceof Monster && livingEntity.getLastDamageCause() != null) {
                if (livingEntity.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.PROJECTILE && livingEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent eventByEntity) {
                    if (eventByEntity.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player killer) {
                        QPlayer qKiller = questManager.getPlayerData().get(killer.getUniqueId());
                        if (qKiller == null) return;

                        QuestData dailyQuestData = qKiller.getDailyQuest();
                        if (dailyQuestData.getQuest().getCondition().job() == Condition.Jobs.HUNTER && dailyQuestData.getQuest().getCondition().type().equalsIgnoreCase("ANY_MONSTER_BOW"))
                            dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
                    }
                }
            }
        }
        else {
            QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());
            if (qPlayer == null) return;

            // vérif pour quête journalière
            QuestData dailyQuestData = qPlayer.getDailyQuest();
            if (dailyQuestData.getQuest().getCondition().job() == Condition.Jobs.HUNTER && !dailyQuestData.isFinished()) {
                EntityType entityType;
                try {
                    entityType = EntityType.valueOf(dailyQuestData.getQuest().getCondition().type());

                    if (entityType == event.getEntity().getType()) {
                        dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
                    }
                } catch (IllegalArgumentException ignored) {
                    // on vérifie si le type de la condition est un mob
                    // si cela renvoie IllegalArgumentException alors ce n'est pas un monstre
                }

                if (dailyQuestData.isFinished()) {
                    dailyQuestData.setFinished(player);
                }
            }

            // vérif pour quête hebdomadaire
            QuestData weeklyQuestData = qPlayer.getWeeklyQuest();
            if (weeklyQuestData.getQuest().getCondition().job() == Condition.Jobs.HUNTER && !weeklyQuestData.isFinished()) {
                if (weeklyQuestData.getQuest().getCondition().type().equalsIgnoreCase("ANY_MONSTER") && livingEntity instanceof Monster) {
                    weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                } else {
                    EntityType entityType;
                    try {
                        entityType = EntityType.valueOf(weeklyQuestData.getQuest().getCondition().type());

                        if (entityType == event.getEntity().getType()) {
                            weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                        }
                    } catch (IllegalArgumentException ignored) {
                        // on vérifie si le type de la condition est un mob
                        // si cela renvoie IllegalArgumentException alors ce n'est pas un monstre
                    }
                }

                if (weeklyQuestData.isFinished()) {
                    weeklyQuestData.setFinished(player);
                }
            }
        }
    }

    @EventHandler
    public void onEntityBread(EntityBreedEvent event) {
        // quête seulement disponible en hebdomadaire
        if (!(event.getBreeder() instanceof Player player)) return;

        QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());
        if (qPlayer == null) return;

        QuestData weeklyQuestData = qPlayer.getWeeklyQuest();
        // quête seulement pour le fermier
        if (weeklyQuestData.getQuest().getCondition().job() != Condition.Jobs.FARMER) return;
        if (weeklyQuestData.isFinished()) return;

        weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
        if (weeklyQuestData.isFinished()) {
            weeklyQuestData.setFinished(player);
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        // quête journalière
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack craftedItem = event.getCurrentItem();
        if (craftedItem == null) return;

        QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());
        if (qPlayer == null) return;

        QuestData dailyQuestData = qPlayer.getDailyQuest();
        // pour l'instant seul le métier d'enchanteur a une quête de craft
        if (dailyQuestData.getQuest().getCondition().job() != Condition.Jobs.ENCHANTER) return;
        if (dailyQuestData.isFinished()) return;

        try {
            Material type = Material.valueOf(dailyQuestData.getQuest().getCondition().type());
            if (craftedItem.getType() == type) {
                dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + getAmount(event, craftedItem));
                if (dailyQuestData.isFinished()) {
                    dailyQuestData.setFinished(player);
                }
            }
        } catch (IllegalArgumentException ignored) { }

    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        // quête seulement disponible en journalière
        Player player = event.getEnchanter();

        QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());
        if (qPlayer == null) return;

        QuestData dailyQuestData = qPlayer.getDailyQuest();
        if (dailyQuestData.getQuest().getCondition().job() != Condition.Jobs.ENCHANTER) return;
        if (dailyQuestData.isFinished()) return;

        dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
        if (dailyQuestData.isFinished()) {
            dailyQuestData.setFinished(player);
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        // quête seulement disponible en journalière
        for (ItemStack itemStack : event.getResults()) {
            if (itemStack == null) continue;
            Location location = event.getBlock().getLocation();

            for (Entity entity : event.getBlock().getWorld().getNearbyEntities(location, 4, 4, 4)) {
                if (entity instanceof Player player) {
                    QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());
                    if (qPlayer == null) continue;

                    QuestData dailyQuestData = qPlayer.getDailyQuest();
                    if (dailyQuestData.getQuest().getCondition().job() != Condition.Jobs.ALCHEMIST) continue;
                    if (dailyQuestData.isFinished()) continue;

                    dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
                    if (dailyQuestData.isFinished()) {
                        dailyQuestData.setFinished(player);
                    }
                    break;
                }
            }
        }
    }

    private int getAmount(CraftItemEvent event, ItemStack craftedItem) {
        int resultPerCraft = craftedItem.getAmount();

        if (event.isShiftClick()) {
            ItemStack[] matrix = event.getInventory().getMatrix();
            int minCrafts = Integer.MAX_VALUE;

            for (ItemStack ingredient : matrix) {
                if (ingredient != null && !ingredient.getType().isAir()) {
                    int required = 1;
                    minCrafts = Math.min(minCrafts, ingredient.getAmount() / required);
                }
            }

            return minCrafts * resultPerCraft;
        }

        return resultPerCraft;
    }

}