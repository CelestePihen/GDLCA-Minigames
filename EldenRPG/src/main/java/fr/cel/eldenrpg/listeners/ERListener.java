package fr.cel.eldenrpg.listeners;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.utils.Replacement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.time.Duration;

public class ERListener extends Replacement implements Listener {

    protected final EldenRPG main;

    public ERListener(EldenRPG main) {
        this.main = main;
    }

}