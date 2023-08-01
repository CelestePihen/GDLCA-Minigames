package fr.cel.eldenrpg.config;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.npc.NPC;
import fr.cel.eldenrpg.manager.npc.NPCConfigurationException;
import fr.cel.eldenrpg.manager.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigNPC {

    private final String name;
    private final EldenRPG main;
    
    public ConfigNPC(EldenRPG main, String name) {
        this.name = name;
        this.main = main;
    }

    public NPC getNPC() {
        File file = new File(main.getDataFolder() + File.separator + "npcs", name + ".yml");
        if (file.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);

                String displayName = config.getString("displayName");

                String skinTexture = config.getString("skin.texture");
                String skinSignature = config.getString("skin.signature");

                Location location = loadLocation(config);

                return new NPC(name, displayName, location, skinTexture, skinSignature, main);
            } catch (IOException | InvalidConfigurationException e) {
                throw new NPCConfigurationException("Erreur lors du chargement du NPC " + name, e);
            }
        }

        return null;
    }

    private Location loadLocation(YamlConfiguration config) {
        double x = config.getDouble("location.x");
        double y = config.getDouble("location.y");
        double z = config.getDouble("location.z");

        return new Location(Bukkit.getWorld("world"), x, y, z);
    }
    
}