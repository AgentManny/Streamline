package gg.manny.streamline.handler;

import gg.manny.streamline.Streamline;
import gg.manny.streamline.npc.NPC;
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
            for (NPC npc : npcManager.registeredNPCs) {
                Location npcLoc = npc.getLocation();
                if (npc.getLocation().getWorld().equals(player.getWorld())) {
                    double x = Math.abs(to.getX() - npcLoc.getX());
                    double z = Math.abs(to.getZ() - npcLoc.getZ());
                    boolean visibility = x < NPC.VISIBILITY_RADIUS && z < NPC.VISIBILITY_RADIUS;
                    if (!npc.getObservers().isEmpty() && npc.getObservers().contains(player.getUniqueId())) {
                        if (!visibility) {
                            npc.removeObserver(player);
                            return;
                        }
                        if (x < NPC.LOOK_RADIUS && z < NPC.LOOK_RADIUS) {
                            if (npc.getLooking().contains(player.getUniqueId())) {
                                Location clone = npcLoc.clone();
                                clone.setDirection(to.toVector().subtract(npcLoc.toVector()));
                                float yaw = clone.getYaw();
                                float pitch = clone.getPitch();
                                npc.setLook(player, yaw, pitch);
                            } else {
                                npc.getLooking().add(player.getUniqueId());
                            }
                        } else if (npc.getLooking().remove(player.getUniqueId())) {
                            npc.setLook(player, npcLoc.getYaw(), npcLoc.getPitch());
                        }
                    } else if (visibility) {
                        npc.addObserver(player);
                    }
                }
            }
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location to, Location from, PacketPlayInFlying packet) {

    }
}
