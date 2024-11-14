package fr.cel.gameapi.utils;

import lombok.Getter;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe sert au Pack de Ressources
 */
@Getter
public final class RPUtils {

    private final Map<String, CustomMusic> musics = new HashMap<>();

    public RPUtils() {
        for (CustomMusic customMusic : CustomMusic.values()) {
            musics.put(customMusic.getMusicName(), customMusic);
        }
    }

    @Getter
    public enum CustomMusic {
        // Custom Music
        MASTERMIND_CHASE("The Mastermind Chase", "Dead by Daylight", Sound.MUSIC_DISC_11),
        GIGACHAD("Gigachad Theme", null, "Original Music: Can You Feel My Heart - Bring Me The Horizon", Sound.MUSIC_DISC_13),
        LEFT_BEHIND("Left Behind", "DAGames / CG5 / 8-BitRyan / Trickywi", Sound.MUSIC_DISC_BLOCKS),
        JOJO("Giorno's Theme (il vento d'oro)", "Yuugo Kanno", "Jojo's Bizarre Adventure", Sound.MUSIC_DISC_CAT),
        AMOGUS("Among Us - Drip Theme", "Leonz", Sound.MUSIC_DISC_CHIRP),
        SPOOKY_SCARY_SKELETONS("Spooky Scary Skeletons Remix", "The Living Tombstone", "Original Music: Andrew Gold - Spooky Scary Skeletons", Sound.MUSIC_DISC_FAR),
        LUIGIS_MANSION("Luigi's Mansion Theme", "FamilyJules", "Original Music: Nintendo - Kazumi Totaka", Sound.MUSIC_DISC_MALL),
        SNK_ASHES_FIRE("Ashes on The Fires", "Kohta Yamamoto", "L'Attaque des Titans",  Sound.MUSIC_DISC_MELLOHI),
        SKIBIDIBOP("Skibidibop", null, "Source: Tiktok", Sound.MUSIC_DISC_STAL),
        RICK_ROLL("Never Gonna Give You Up", "Rick Astley", Sound.MUSIC_DISC_STRAD),
        LA_ROJA("La Roja", "Amine / MisterV", Sound.MUSIC_DISC_WAIT),
        OPENING_CHAINSAW_MAN("KICK BACK", "Kenshin Yonezu", "Chainsaw Man", Sound.MUSIC_DISC_WARD),

        // Minecraft Music (Unmodified Music)
        PIGSTEP("Pigstep", "Lena Raine", Sound.MUSIC_DISC_PIGSTEP),
        OTHERSIDE("otherside", "Lena Raine", Sound.MUSIC_DISC_OTHERSIDE),
        WARDEN("5", "Samuel Âberg", Sound.MUSIC_DISC_5),
        RELIC("Relic", "Aaron Cherof", Sound.MUSIC_DISC_RELIC),
        CREATOR("Creator", "Lena Raine", Sound.MUSIC_DISC_CREATOR),
        CREATOR_MUSIC_BOX("Creator Music Box", "Lena Raine", Sound.MUSIC_DISC_CREATOR_MUSIC_BOX),
        PRECIPICE("Precipice", "Aaron Cherof", Sound.MUSIC_DISC_PRECIPICE),
        ;

        private final String musicName;
        private final String author;
        private final String description;
        private final Sound sound;

        CustomMusic(String musicName, String author, Sound sound) {
            this(musicName, author, null, sound);
        }

        CustomMusic(String musicName, String author, String description, Sound sound) {
            this.musicName = musicName;
            this.author = author;
            this.description = description;
            this.sound = sound;
        }

    }

    @Getter
    public enum Advancements {
        // Hub
        BIENVENUE("bienvenue"),
        DJ("dj"),

        // Mini-Jeux Général
        PREMIER_REMERCIEMENT("premier_remerciement"),
        BAH_VOYONS("bah_voyons"),
        ;

        private final String advancementName;

        Advancements(String advancementName) {
            this.advancementName = advancementName;
        }
    }

}