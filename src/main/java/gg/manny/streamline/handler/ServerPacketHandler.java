package gg.manny.streamline.handler;

import gg.manny.streamline.Streamline;
import gg.manny.streamline.npc.NPC;
import gg.manny.streamline.npc.NPCManager;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import rip.thecraft.server.handler.PacketHandler;

@RequiredArgsConstructor
public class ServerPacketHandler implements PacketHandler {

    private final Streamline plugin;
    private final NPCManager npcManager;

    @Override
    public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        String packetName = packet.getClass().getSimpleName();
        if (packetName.equals("PacketPlayInUseEntity")) {
            PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            for (NPC registeredNPC : npcManager.registeredNPCs) {
                if (useEntity.getEntityId() == registeredNPC.entityId) {
                    registeredNPC.interact(playerConnection.player, useEntity);
                }
            }
        }
    }

    @Override
    public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {

    }
}
