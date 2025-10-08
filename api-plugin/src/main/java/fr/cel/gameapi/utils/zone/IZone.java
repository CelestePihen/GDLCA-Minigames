package fr.cel.gameapi.utils.zone;

import java.util.List;
import java.util.UUID;

public interface IZone {

    /**
     * Starts checking the zone for players.
     * @param playersInGame List of UUIDs of players to track
     */
    void startChecking(List<UUID> playersInGame);

    /**
     * Stops checking the zone.
     */
    void stopChecking();

}