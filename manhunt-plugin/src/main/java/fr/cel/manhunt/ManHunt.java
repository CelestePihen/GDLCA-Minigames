package fr.cel.manhunt;

import fr.cel.manhunt.commands.HuntCommand;
import fr.cel.manhunt.listener.ManHuntListener;
import fr.cel.manhunt.utils.Colorize;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

public class ManHunt extends JavaPlugin {

    @Getter private String prefix = Colorize.format("&6[ManHunt] &r&f");

    private List<UUID> man = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();

        getCommand("manhunt").setExecutor(new HuntCommand(this));
        getServer().getPluginManager().registerEvents(new ManHuntListener(this), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void addMan(UUID uuid) {
        this.man.add(uuid);
    }

    public List<UUID> getMan() {
        return man;
    }

}