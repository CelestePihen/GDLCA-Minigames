package fr.cel.cachecache.map;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.*;

public class WinterUtility {

    @Getter @Setter private List<Location> giftLocations = new ArrayList<>();
    @Getter private final Map<UUID, Boolean> playersOpenedGift = new HashMap<>();

    @Getter @Setter private List<Location> chrismasTreeDepositLocations = new ArrayList<>();

}
