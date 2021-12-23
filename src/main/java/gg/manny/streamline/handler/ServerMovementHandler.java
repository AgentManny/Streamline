package gg.manny.streamline.handler;

import gg.manny.streamline.Streamline;
import gg.manny.streamline.npc.NPCManager;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.server.handler.MovementHandler;

@RequiredArgsConstructor
public class ServerMovementHandler implements MovementHandler {

    private final Streamline plugin;
    private final NPCManager npcManager;

    @Override
    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packet) {
        if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
            npcManager.check(player, player.getLocation(), false);
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location to, Location from, PacketPlayInFlying packet) {

    }
}
