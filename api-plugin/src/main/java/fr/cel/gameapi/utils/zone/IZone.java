package fr.cel.gameapi.utils.zone;

import java.util.List;
import java.util.UUID;

public interface IZone {
    void startChecking(List<UUID> playersInGame);
    void stopChecking();
}