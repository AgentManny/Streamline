package gg.manny.streamline.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

@UtilityClass
public class PlayerUtils {

    private final boolean PROTOCOL_SUPPORT;

    static {
        PROTOCOL_SUPPORT = Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport");
    }

    /**
     * Gets the protocol version of a player
     *
     * @param player Player to check
     * @return Player's protocol version
     */
    public int getProtocolVersion(Player player) {
        if (PROTOCOL_SUPPORT) {
            return ProtocolSupportAPI.getProtocolVersion(player).getId();
        }
        return player.getProtocolVersion();
    }

    /**
     * Gets if a player is on a legacy version
     *
     * @param player Player to check
     * @return If player is on legacy
     */
    public boolean onLegacyVersion(Player player) {
        if (PROTOCOL_SUPPORT) {
            return ProtocolSupportAPI.getProtocolVersion(player).isBefore(ProtocolVersion.MINECRAFT_1_8);
        }
        return getProtocolVersion(player) >= 5;
    }

    /**
     * Sends a packet to a player
     * @param player Player to send
     * @param packet Packet to send
     */
    public void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
