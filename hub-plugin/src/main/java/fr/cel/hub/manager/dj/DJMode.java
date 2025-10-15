package fr.cel.hub.manager.dj;

import lombok.Getter;

@Getter
public enum DJMode {
    SINGLE("Ordre normal"), PLAYLIST_LOOP("Boucle de Playlist"), SHUFFLE("Aléatoire");

    private final String displayName;

    DJMode(String displayName) {
        this.displayName = displayName;
    }
}