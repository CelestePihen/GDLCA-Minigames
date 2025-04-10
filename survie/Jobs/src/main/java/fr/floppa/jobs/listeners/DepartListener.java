package fr.floppa.jobs.listeners;

import fr.floppa.jobs.Jobs;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.function.Consumer;

public class DepartListener implements Listener {

    private final Jobs main;

    private final Location corner1 = new Location(Bukkit.getWorld("world"), 2, -56, 9);
    private final Location corner2 = new Location(Bukkit.getWorld("world"), -2, -60, 10);
    private final Location secret = new Location(Bukkit.getWorld("world"), 0.5, -60, -7.5);
    private final Location tutorial = new Location(Bukkit.getWorld("world"), -12.5, -60, 10.5, 180, 0);
    private final Location spawn = new Location(Bukkit.getWorld("world"), -1367, 88, -320);

    private final Map<UUID, Boolean> hasFinishedTutorials = new HashMap<>();

    public DepartListener(Jobs main){
        this.main = main;
        registerClassActions();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // si c'est la première fois que le joueur rejoint
        if (!player.hasPlayedBefore()) {
            hasFinishedTutorials.put(player.getUniqueId(), false);

            // cache tous les joueurs au joueur qui rejoint
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                player.hidePlayer(main, other);
            }

            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                other.hidePlayer(main, player);
            }

            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(secret);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, -1, 1, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, -1, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, -1, 1, false, false));
        }

        main.loadPlayerJob(player);

        if (!hasFinishedTutorials.get(player.getUniqueId())) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                player.hidePlayer(main, other);
            }

            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                other.hidePlayer(main, player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        main.savePlayerjob(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location loc = event.getTo();
        if (loc == null) return;

        if (isInZone(loc, corner1, corner2)) {
            player.teleport(tutorial);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 3*20, 1, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3*20, 1, false, false));
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (hasFinishedTutorials.get(player.getUniqueId())) return;
        event.setFoodLevel(20);
    }

    private final Map<Material, Consumer<Player>> classActions = new HashMap<>();

    public void registerClassActions() {
        classActions.put(Material.IRON_SWORD, p -> {
            setUpPlayer(p, 14, new ItemStack(Material.STONE_SWORD));
        });

        classActions.put(Material.MAP, p -> {
            setUpPlayer(p, 10,
                    new ItemStack(Material.MAP, 2),
                    new ItemStack(Material.CHERRY_BOAT));
        });

        classActions.put(Material.COOKED_BEEF, p -> {
            setUpPlayer(p, 10,
                    new ItemStack(Material.COOKED_BEEF, 8),
                    new ItemStack(Material.COOKED_CHICKEN, 8),
                    new ItemStack(Material.FURNACE));
        });

        classActions.put(Material.IRON_HOE, p -> {
            setUpPlayer(p, 10,
                    new ItemStack(Material.STONE_HOE),
                    new ItemStack(Material.MELON_SEEDS, 4),
                    new ItemStack(Material.BEETROOT_SEEDS, 4));
        });

        classActions.put(Material.IRON_AXE, p -> {
            ItemStack axe = new ItemStack(Material.STONE_AXE);
            ItemMeta meta = axe.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.EFFICIENCY, 2, true);
                axe.setItemMeta(meta);
            }
            setUpPlayer(p, 10, axe);
        });

        classActions.put(Material.FISHING_ROD, p -> {
            ItemStack rod = new ItemStack(Material.FISHING_ROD);
            ItemMeta meta = rod.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 7, true);
                rod.setItemMeta(meta);
            }
            setUpPlayer(p, 10, rod);
        });

        classActions.put(Material.ANVIL, p -> {
            p.setLevel(4);
            setUpPlayer(p, 10, new ItemStack(Material.ANVIL));
        });

        classActions.put(Material.BOOK, p -> {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            if (meta != null) {
                List<Enchantment> list = new ArrayList<>(List.of(Enchantment.values()));
                Collections.shuffle(list);
                Random r = new Random();
                for (int i = 0; i < Math.min(5, list.size()); i++) {
                    Enchantment e = list.get(i);
                    meta.addStoredEnchant(e, r.nextInt(e.getMaxLevel()) + 1, true);
                }
                book.setItemMeta(meta);
            }
            setUpPlayer(p, 10, book);
        });

        classActions.put(Material.BARRIER, p -> {
            setUpPlayer(p, 2);
        });
    }

    private void setUpPlayer(Player player, double health, ItemStack... items) {
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(spawn);
        player.setRespawnLocation(spawn);
        player.closeInventory();
        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        player.setHealth(health);
        player.setFoodLevel(20);
        if (items != null) player.getInventory().addItem(items);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                player.showPlayer(main, other);
                other.showPlayer(main, player);
            }
        }

        hasFinishedTutorials.put(player.getUniqueId(), true);
        addJobs(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase("Choisissez votre kit au départ !")) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        if (current == null || current.getType() == Material.AIR) return;

        Consumer<Player> action = classActions.get(current.getType());
        if (action != null) {
            event.setCancelled(true);
            action.accept(player);
        }
    }

    public Map<UUID, Boolean> getHasFinishedTutorials() {
        return hasFinishedTutorials;
    }

    public boolean isInZone(Location loc, Location a, Location b) {
        int x1 = Math.min(a.getBlockX(), b.getBlockX());
        int x2 = Math.max(a.getBlockX(), b.getBlockX());
        int y1 = Math.min(a.getBlockY(), b.getBlockY());
        int y2 = Math.max(a.getBlockY(), b.getBlockY());
        int z1 = Math.min(a.getBlockZ(), b.getBlockZ());
        int z2 = Math.max(a.getBlockZ(), b.getBlockZ());

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return x >= x1 && y >= y1 && z >= z1 &&
               x <= x2 && y <= y2 && z <= z2;
    }

    private void addJobs(Player player) {
        main.getAlchimisteManager().addAlchimiste(player, 0, 1);
        main.getChasseurManager().addChasseur(player, 0, 1);
        main.getEnchanteurManager().addEnchanteur(player, 0, 1);
        main.getExplorateurManager().addExplorateur(player, 0, 1);
        main.getFermierManager().addFermier(player, 0, 1);
        main.getMineurManager().addMineur(player, 0, 1);
    }

}