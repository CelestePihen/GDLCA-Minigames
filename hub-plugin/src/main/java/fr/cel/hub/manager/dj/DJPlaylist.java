package fr.cel.hub.manager.dj;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DJPlaylist {

    private final String playlistName;
    private final List<DJMusic> musics = new ArrayList<>();

    public DJPlaylist(String playlistName) {
        this.playlistName = playlistName;
    }

    public void addMusic(DJMusic music) {
        musics.add(music);
    }

}