package fr.cel.gameapi.utils;

import lombok.Getter;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Map;

public final class RPUtils {

    @Getter private static final Map<String, CustomMusic> musics = new HashMap<>();

    public static void registerMusics() {
        for (CustomMusic customMusic : CustomMusic.values()) {
            if (customMusic.customSound == null) {
                musics.put(customMusic.musicName, customMusic);
            }
        }
    }

    @Getter
    public enum CustomMusic {
        // Custom Music
        AMOGUS("Among Us - Drip Theme", "Leonz", "gdlca:music_disc.amogusmeme"),
        CHAINSAW_MAN("KICK BACK", "Kenshin Yonezu", "Chainsaw Man", "gdlca:music_disc.chainsawmankickback"),
        LUIGIS_MANSION("Luigi's Mansion Theme [Remix]", "Qumu", "Original Music: Nintendo - Kazumi Totaka", "gdlca:music_disc.luigismansionremix"),
        RICK_ROLL("Never Gonna Give You Up", "Rick Astley", "gdlca:music_disc.rickroll"),
        SPECIALZ("SpecialZ", "King Gnu", "Jujustu Kaisen Season 2", "gdlca:music_disc.specialz"),
        SPOOKY_SCARY_SKELETONS("Spooky Scary Skeletons (Remix)", "The Living Tombstone", "Original Music: Andrew Gold", "gdlca:music_disc.spookyscaryskeketonsremix"),
        STUCK_INSIDE("Stuck Inside", "Living Tombstone - CG5 - Black Gryph0n - Baasik - Kevin Foster", "gdlca:music_disc.stuckinside"),

        // Minecraft Music (Unmodified Music)
        PIGSTEP("Pigstep", "Lena Raine", Sound.MUSIC_DISC_PIGSTEP),
        OTHERSIDE("otherside", "Lena Raine", Sound.MUSIC_DISC_OTHERSIDE),
        RELIC("Relic", "Aaron Cherof", Sound.MUSIC_DISC_RELIC),
        CREATOR("Creator", "Lena Raine", Sound.MUSIC_DISC_CREATOR),
        CREATOR_MUSIC_BOX("Creator Music Box", "Lena Raine", Sound.MUSIC_DISC_CREATOR_MUSIC_BOX),
        PRECIPICE("Precipice", "Aaron Cherof", Sound.MUSIC_DISC_PRECIPICE)
        ;

        private final String musicName;
        private final String author;
        private final String description;
        private final Sound vanillaSound;
        private final String customSound;

        CustomMusic(String musicName, String author, Sound sound) {
            this(musicName, author, null, sound);
        }

        CustomMusic(String musicName, String author, String sound) {
            this(musicName, author, null, sound);
        }

        CustomMusic(String musicName, String author, String description, Sound sound) {
            this.musicName = musicName;
            this.author = author;
            this.description = description;
            this.vanillaSound = sound;
            this.customSound = null;
        }

        CustomMusic(String musicName, String author, String description, String sound) {
            this.musicName = musicName;
            this.author = author;
            this.description = description;
            this.vanillaSound = null;
            this.customSound = sound;
        }

    }

}