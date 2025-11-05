package fr.cel.gameapi.manager.npc;

import net.kyori.adventure.key.Key;
import org.bukkit.profile.PlayerTextures;

public record Skin(Key body, Key cape, Key elytra, PlayerTextures.SkinModel model) {
}
