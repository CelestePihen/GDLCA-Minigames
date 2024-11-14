package fr.cel.halloween.map.state.game;

import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.providers.StateListenerProvider;
import fr.cel.halloween.map.providers.game.PlayingListenerProvider;
import fr.cel.halloween.map.state.MapState;
import fr.cel.halloween.map.timer.game.ChestMapTask;
import fr.cel.halloween.map.timer.game.PlayingMapTask;
import fr.cel.halloween.map.timer.game.RemoveBlindnessTask;
import fr.cel.halloween.map.timer.game.SoulsMapTask;
import lombok.Getter;

@Getter
public class PlayingMapState extends MapState {

    private PlayingMapTask playingMapTask;
    private SoulsMapTask soulsMapTask;
    private ChestMapTask chestMapTask;

    public PlayingMapState(HalloweenMap map) {
        super("En partie", map);
    }

    @Override
    public void onEnable(HalloweenEvent main) {
        super.onEnable(main);

        playingMapTask = new PlayingMapTask(getMap());
        playingMapTask.runTaskTimer(main, 0, 20);

        soulsMapTask = new SoulsMapTask(getMap());
        soulsMapTask.runTaskTimer(main, 0, 6*20);

        chestMapTask = new ChestMapTask(getMap());
        chestMapTask.runTaskTimer(main, 0, 120*20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (playingMapTask != null) playingMapTask.cancel();
        if (soulsMapTask != null) soulsMapTask.cancel();
        if (chestMapTask != null) chestMapTask.cancel();
    }

    @Override
    public StateListenerProvider getListenerProvider() {
        return new PlayingListenerProvider(getMap());
    }

}