package fr.cel.gameapi.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class RPUtils {

    @Getter private final Map<String, CustomMusic> musics = new HashMap<>();

    public void registerMusics() {
        for (CustomMusic customMusic : CustomMusic.values()) {
            musics.put(customMusic.getMusicName(), customMusic);
        }
    }

    // TODO le transformer avec des fichiers de config
    @Getter
    public enum CustomMusic {
        // Custom Music
        CHAINSAW_MAN("KICK BACK", "Kenshin Yonezu", "Chainsaw Man", "gdlca:music_disc.chainsawmankickback"),
        COE33_LUMIERE("Lumière - Clair Obscur: Expedition 33", "Lorien Testard - Alice Duport-Percier", "gdlca:music_disc.coe33_lumiere"),
        LUIGIS_MANSION("Luigi's Mansion Theme [Remix]", "Qumu", "Original Music: Nintendo - Kazumi Totaka", "gdlca:music_disc.luigismansionremix"),
        RICK_ROLL("Never Gonna Give You Up", "Rick Astley", "gdlca:music_disc.rickroll"),
        SPECIALZ("SpecialZ", "King Gnu", "Jujustu Kaisen Season 2", "gdlca:music_disc.specialz"),
        SPOOKY_SCARY_SKELETONS("Spooky Scary Skeletons (Remix)", "The Living Tombstone", "Original Music: Andrew Gold", "gdlca:music_disc.spookyscaryskeketonsremix"),
        STUCK_INSIDE("Stuck Inside", "Living Tombstone - CG5 - Black Gryph0n - Baasik - Kevin Foster", "Musique inspiré du film Five Nights at Freddy's", "gdlca:music_disc.stuckinside"),

        // Minecraft Music
        PIGSTEP("Pigstep", "Lena Raine", "minecraft:music_disc.pigstep"),
        OTHERSIDE("otherside", "Lena Raine", "minecraft:music_disc.otherside"),
        RELIC("Relic", "Aaron Cherof", "minecraft:music_disc.relic"),
        CREATOR("Creator", "Lena Raine", "minecraft:music_disc.creator"),
        CREATOR_MUSIC_BOX("Creator Music Box", "Lena Raine", "minecraft:music_disc.creator_music_box"),
        PRECIPICE("Precipice", "Aaron Cherof", "minecraft:music_disc.precipice"),
        TEARS("Tears", "Amos Roddy", "minecraft:music_disc.tears"),
        LAVA_CHICKEN("Lava Chicken", "Hyper Potions", "minecraft:music_disc.lava_chicken");

        private final String musicName;
        private final String author;
        private final String description;
        private final String customSound;

        CustomMusic(String musicName, String author, String sound) {
            this(musicName, author, null, sound);
        }

        CustomMusic(String musicName, String author, String description, String sound) {
            this.musicName = musicName;
            this.author = author;
            this.description = description;
            this.customSound = sound;
        }

    }

}