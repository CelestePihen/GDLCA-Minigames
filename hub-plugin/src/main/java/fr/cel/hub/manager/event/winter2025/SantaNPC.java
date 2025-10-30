package fr.cel.hub.manager.event.winter2025;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.npc.NPC;
import fr.cel.gameapi.manager.npc.Skin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SantaNPC extends NPC {

    // TODO: change location when map will be finished
    public SantaNPC() {
        super(
                "santa",
                "Père Noël",
                new Location(Bukkit.getWorlds().getFirst(), 333.5, 115, 106.5),
                true,
                new Skin("ewogICJ0aW1lc3RhbXAiIDogMTc2MTgzNDkyMzYyOCwKICAicHJvZmlsZUlkIiA6ICI3ZTQ0MWE1OTllOTE0ZjM1YTJmMzMwNjM2NzZjNWVhMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTYW50YXNDb21pbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDE3OGFlMDNkODQyMjdmMjFmYTIzNjgyZDljNjIwMTMxMDAwZmZiNzY3MzM5NTUzM2IyNzQwZmE4M2FiMTAzNyIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzY1OGM1MDI1Yzc3Y2ZhYzc1NzRhYWIzYWY5NGE0NmE4ODg2ZTNiNzcyMmE4OTUyNTVmYmYyMmFiODY1MjQzNCIKICAgIH0KICB9Cn0=",
                        "x94X2KyyYmQrxKk62qsZpRUx81YLsK/jyIHWf/AP4cV/9hnJ9TEAhYcgpWI/Bv0aXGSEapXlm6NtPvPR8G06f9Uvo7YL672Di6BtvZxHaU3AvhzL/H+ER3sIt/q+n0cItbRyytIQZlk+hElevRDg8WPMFOO5xTIP1bUfCn7t8aJcKbCCbHiQs669EH+nJCNYEBUVUEOosv1kaC09CqEfBPkkSSYf9dDiPgEgr6n2iaTXRJG8LzDwZAxUuK4uX48WJ6/mSBR+DgsXKuWzrQlVq+BvWqTnGsIFLkCw3NRYsEp46bApcwsDqpWXiYMkQnz9aaLTWZOIAlDOn0AljQ8YfWDLR9rJOzJIu1MGr9EK4EhW3Us0NdHbEGRApoC+YEFrO7rgc/cLUMNJZVWocxgmfwBqrbExlT+8HQfa6HlrLBPOS6xSqH9ERGiedJkVKzYk3BLOR35s9ygKZZmXwk2Ayrsixh7m2wRG0ULxSfhNAWMInE3X+jJyMTd/NH8pluKvmwfaz/2Nl2LUKyZnMG+Ited3tFHzUvP63yWzRMdWF6LxS2bUWKIONjFQOMUIIh/tXmRvDTEBuNJ/i5WCwm9CCH5KQ6TXasLgjXpp/yYhwLLKLA7In2jdtrA9LAvkCmGSkOk6HVk2Knn+mh6lxFXgcA/W949syCLeSqc7zbbsIoA=")
        );
    }

    @Override
    public void interact(Player player) {
        this.chat("Ho Ho Ho ! Joyeux Noël " + player.getName() + " !", player);
        GameAPI.getInstance().getInventoryManager().openInventory(new SantaInventory(player), player);
    }

}