package fr.floppa.jobs;

import fr.floppa.jobs.commands.*;
import fr.floppa.jobs.items.HPUpgrade;
import fr.floppa.jobs.job.alchimiste.AlchimisteExpEvent;
import fr.floppa.jobs.job.alchimiste.AlchimisteManager;
import fr.floppa.jobs.job.alchimiste.AlchimisteRestrictionItems;
import fr.floppa.jobs.job.chasseur.ChasseurExpEvent;
import fr.floppa.jobs.job.chasseur.ChasseurManager;
import fr.floppa.jobs.job.chasseur.ChasseurRestrictionItems;
import fr.floppa.jobs.job.enchanteur.EnchanteurExpEvent;
import fr.floppa.jobs.job.enchanteur.EnchanteurManager;
import fr.floppa.jobs.job.enchanteur.EnchanteurRestrictionItems;
import fr.floppa.jobs.job.explorateur.ExplorateurExpEvent;
import fr.floppa.jobs.job.explorateur.ExplorateurManager;
import fr.floppa.jobs.job.explorateur.ExplorateurRestrictionItems;
import fr.floppa.jobs.job.fermier.FermierExpEvent;
import fr.floppa.jobs.job.fermier.FermierManager;
import fr.floppa.jobs.job.fermier.FermierRestrictionItems;
import fr.floppa.jobs.job.mineur.MineurExpEvent;
import fr.floppa.jobs.job.mineur.MineurManager;
import fr.floppa.jobs.job.mineur.MineurRestrictionItems;
import fr.floppa.jobs.listeners.DepartListener;
import fr.floppa.jobs.listeners.JobsListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Jobs extends JavaPlugin {

    private AlchimisteManager alchimisteManager;
    private ChasseurManager chasseurManager;
    private EnchanteurManager enchanteurManager;
    private ExplorateurManager explorateurManager;
    private FermierManager fermierManager;
    private MineurManager mineurManager;

    private DepartListener departListener;

    @Override
    public void onEnable() {
        addNotchAppleRecipe();

        this.alchimisteManager = new AlchimisteManager();
        this.chasseurManager = new ChasseurManager();
        this.enchanteurManager = new EnchanteurManager();
        this.explorateurManager = new ExplorateurManager();
        this.fermierManager = new FermierManager();
        this.mineurManager = new MineurManager();

        // commandes
        this.getCommand("help").setExecutor(new HelpCommand());
        this.getCommand("jafar").setExecutor(new JafarCommand());
        this.getCommand("resetlevel").setExecutor(new ResetLevelCommand(this));
        this.getCommand("kitdepart").setExecutor(new KitDepartCommand(this));

        this.getCommand("metiers").setExecutor(new JobsCommand());
        this.getCommand("jobs").setExecutor(new JobsCommand());

        this.getCommand("level").setExecutor(new LevelCommand(this));
        this.getCommand("niveau").setExecutor(new LevelCommand(this));

        // événements
        getServer().getPluginManager().registerEvents(new HPUpgrade(), this);

        getServer().getPluginManager().registerEvents(new AlchimisteExpEvent(alchimisteManager), this);
        getServer().getPluginManager().registerEvents(new AlchimisteRestrictionItems(alchimisteManager), this);

        getServer().getPluginManager().registerEvents(new ChasseurExpEvent(chasseurManager), this);
        getServer().getPluginManager().registerEvents(new ChasseurRestrictionItems(chasseurManager), this);

        getServer().getPluginManager().registerEvents(new EnchanteurExpEvent(enchanteurManager), this);
        getServer().getPluginManager().registerEvents(new EnchanteurRestrictionItems(enchanteurManager), this);

        getServer().getPluginManager().registerEvents(new ExplorateurExpEvent(explorateurManager), this);
        getServer().getPluginManager().registerEvents(new ExplorateurRestrictionItems(explorateurManager),this);

        getServer().getPluginManager().registerEvents(new FermierExpEvent(fermierManager), this);
        getServer().getPluginManager().registerEvents(new FermierRestrictionItems(fermierManager),this);

        getServer().getPluginManager().registerEvents(new MineurExpEvent(mineurManager), this);
        getServer().getPluginManager().registerEvents(new MineurRestrictionItems(mineurManager), this);

        departListener = new DepartListener(this);
        getServer().getPluginManager().registerEvents(departListener,this);

        getServer().getPluginManager().registerEvents(new JobsListener(),this);
    }

    @Override
    public void onDisable() {

    }

    public AlchimisteManager getAlchimisteManager() {
        return alchimisteManager;
    }

    public ChasseurManager getChasseurManager() {
        return chasseurManager;
    }

    public EnchanteurManager getEnchanteurManager() {
        return enchanteurManager;
    }

    public ExplorateurManager getExplorateurManager() {
        return explorateurManager;
    }

    public FermierManager getFermierManager() {
        return fermierManager;
    }

    public MineurManager getMineurManager() {
        return mineurManager;
    }

    public DepartListener getDepartListener() {
        return departListener;
    }

    /**
     * Ré-ajoute le craft de la pomme de Notch
     */
    private void addNotchAppleRecipe() {
        NamespacedKey key = NamespacedKey.fromString("enchanted_golden_apple", this);
        if (key == null) return;

        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('A', Material.APPLE);

        Bukkit.addRecipe(recipe);
    }

    /**
     * Sauvegarde la progression des métiers du joueur dans son fichier
     * @param player Le joueur
     */
    public void savePlayerjob(Player player){
        File file = new File(getDataFolder() + File.separator + "players", player.getUniqueId() + ".yml");

        // on créé le fichier du joueur s'il existe pas
        createPlayerFile(player, file);

        YamlConfiguration config = new YamlConfiguration();
        try {
            // on charge le fichier de configuration
            config.load(file);

            // on sauvegarde s'il a fini le tutoriel ou pas
            config.set("tutorial", departListener.getHasFinishedTutorials().get(player.getUniqueId()));

            // on sauvegarde chaque métier
            int xpAlchimiste = alchimisteManager.getAlchimiste(player).getExp();
            int levelAlchimiste = alchimisteManager.getAlchimiste(player).getLevel();
            config.set("alchimiste.xp", xpAlchimiste);
            config.set("alchimiste.level", levelAlchimiste);

            int xpChasseur = chasseurManager.getChasseur(player).getExp();
            int levelChasseur = chasseurManager.getChasseur(player).getLevel();
            config.set("chasseur.xp", xpChasseur);
            config.set("chasseur.level", levelChasseur);

            int xpEnchanteur = enchanteurManager.getEnchanteur(player).getExp();
            int levelEnchanteur = enchanteurManager.getEnchanteur(player).getLevel();
            config.set("enchanteur.xp", xpEnchanteur);
            config.set("enchanteur.level", levelEnchanteur);

            int xpExplorateur = explorateurManager.getExplorateur(player).getExp();
            int levelExplorateur = explorateurManager.getExplorateur(player).getLevel();
            config.set("explorateur.xp", xpExplorateur);
            config.set("explorateur.level", levelExplorateur);

            int xpFermier = fermierManager.getFermier(player).getExp();
            int levelFermier = fermierManager.getFermier(player).getLevel();
            config.set("fermier.xp", xpFermier);
            config.set("fermier.level", levelFermier);

            int xpMineur = mineurManager.getMineur(player).getExp();
            int levelMineur = mineurManager.getMineur(player).getLevel();
            config.set("mineur.xp", xpMineur);
            config.set("mineur.level", levelMineur);

            // on sauvegarde le fichier
            config.save(file);
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("Impossible de charger les données de " + player.getName());
        }
    }

    /**
     * Charge la progression des métiers du joueur
     * @param player Le joueur
     */
    public void loadPlayerJob(Player player) {
        File file = new File(getDataFolder() + File.separator + "players", player.getUniqueId() + ".yml");

        // on créé le fichier du joueur s'il existe pas
        createPlayerFile(player, file);

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);

            // on charge s'il a fini le tutoriel ou pas
            departListener.getHasFinishedTutorials().put(player.getUniqueId(), config.getBoolean("tutorial"));

            // on charge chaque métier
            int xpAlchimiste = config.getInt("alchimiste.xp");
            int levelAlchimiste = config.getInt("alchimiste.level");
            alchimisteManager.addAlchimiste(player, xpAlchimiste, levelAlchimiste);

            int xpChasseur = config.getInt("chasseur.xp");
            int levelChasseur = config.getInt("chasseur.level");
            chasseurManager.addChasseur(player, xpChasseur, levelChasseur);

            int xpEnchanteur = config.getInt("enchanteur.xp");
            int levelEnchanteur = config.getInt("enchanteur.level");
            enchanteurManager.addEnchanteur(player, xpEnchanteur, levelEnchanteur);

            int xpExplorateur = config.getInt("explorateur.xp");
            int levelExplorateur = config.getInt("explorateur.level");
            explorateurManager.addExplorateur(player, xpExplorateur, levelExplorateur);

            int xpFermier = config.getInt("fermier.xp");
            int levelFermier = config.getInt("fermier.level");
            fermierManager.addFermier(player, xpFermier, levelFermier);

            int xpMineur = config.getInt("mineur.xp");
            int levelMineur = config.getInt("mineur.level");
            mineurManager.addMineur(player, xpMineur, levelMineur);
        } catch (IOException | InvalidConfigurationException e){
            System.out.println("Impossible de charger les données de " + player.getName());
        }
    }

    /**
     * Créé le fichier de métiers du joueur
     * @param player Le joueur
     * @param file Le fichier du joueur
     */
    private void createPlayerFile(Player player, File file){
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Le fichier pour le joueur " + player.getName() + " n'a pas pu etre créé !");
            }
        }
    }

}