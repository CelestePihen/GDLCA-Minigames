package fr.cel.kickban;

import org.bukkit.plugin.java.JavaPlugin;

import fr.cel.kickban.listener.KickBanListener;
import fr.cel.kickban.utils.Colorize;

public class KickBan extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(new KickBanListener(this), this);
    }

    @Override
    public void onDisable() {}

    public String getPrefix() {
        return Colorize.format("&6[Kick-Ban] &f");
    }

}