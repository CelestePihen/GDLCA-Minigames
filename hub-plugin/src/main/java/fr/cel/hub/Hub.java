package fr.cel.hub;

import fr.cel.gameapi.manager.command.CommandsManager;
import fr.cel.gameapi.manager.npc.NPCManager;
import fr.cel.hub.commands.EventCommand;
import fr.cel.hub.commands.HubCommand;
import fr.cel.hub.listener.ChatListener;
import fr.cel.hub.listener.ItemListener;
import fr.cel.hub.listener.PlayerListener;
import fr.cel.hub.manager.HubNPCManager;
import fr.cel.hub.manager.dj.DJManager;
import fr.cel.hub.manager.event.winter2025.HeadManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hub extends JavaPlugin {

    @Getter private static Hub instance;
    @Getter private final static Component prefix = Component.empty().append(Component.text("[Hub]", NamedTextColor.GOLD)).append(Component.text(" "));

    @Getter private NPCManager npcManager;
    @Getter private DJManager djManager;
    @Getter private HeadManager headManager;

    /**
     * Se déclenche quand le plugin démarre
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        this.npcManager = new HubNPCManager(this);
        this.npcManager.loadNPCs();

        this.djManager = new DJManager(this);
        this.headManager = new HeadManager(this);

        registerListeners();
        registerCommands();
    }

    /**
     * Se déclenche quand le plugin s'éteint
     */
    @Override
    public void onDisable() {
    }

    /**
     * Fonction qui permet d'enregistrer les listeners
     */
    private void registerListeners() {
        new PlayerListener(this);
        new ChatListener(this);
        new ItemListener(this);
    }

    /**
     * Fonction qui permet d'enregistrer les commandes
     */
    private void registerCommands() {
        CommandsManager commandsManager = new CommandsManager(this);
        commandsManager.addCommand("hub", new HubCommand());
        commandsManager.addCommand("event", new EventCommand());
        commandsManager.addCommand("calculheads", new EventCommand());
    }

}