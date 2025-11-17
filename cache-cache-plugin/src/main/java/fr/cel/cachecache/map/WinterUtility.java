package fr.cel.cachecache.map;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.*;

public class WinterUtility {

    @Getter @Setter private List<Location> giftLocations = new ArrayList<>();
    @Getter @Setter private List<Location> chrismasTreeDepositLocations = new ArrayList<>();

    @Getter private final Set<UUID> playersOpenedGift = new HashSet<>();
    @Getter private final Set<UUID> playerAlreadyClaimedGift = new HashSet<>();

    public void clearPlayersGift() {
        playersOpenedGift.clear();
        playerAlreadyClaimedGift.clear();
    }

}
