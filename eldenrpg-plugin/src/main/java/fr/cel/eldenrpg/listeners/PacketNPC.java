package fr.cel.eldenrpg.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.npc.NPCManager;
import fr.cel.eldenrpg.manager.player.ERPlayer;
import org.bukkit.entity.Player;

public class PacketNPC extends PacketAdapter {

    public PacketNPC() {
        super(EldenRPG.getEldenRPG(), PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        ERPlayer erPlayer = EldenRPG.getEldenRPG().getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());

        if (erPlayer == null) {
            player.sendMessage("Erreur avec votre profil ! Merci de contacter un administrateur.");
            return;
        }

        PacketContainer packet = event.getPacket();
        int entityId = packet.getIntegers().read(0);

        EnumWrappers.Hand hand = packet.getEnumEntityUseActions().read(0).getHand();
        EnumWrappers.EntityUseAction action = packet.getEnumEntityUseActions().read(0).getAction();

        NPCManager.getNpcs().forEach((name, npc) -> {
            if (entityId == npc.getId()) {
                if (npc.isDead()) {
                    return;
                }

                if (hand == EnumWrappers.Hand.MAIN_HAND && action == EnumWrappers.EntityUseAction.INTERACT) {
                    npc.interact(player, erPlayer);
                }
            }
        });
    }

}