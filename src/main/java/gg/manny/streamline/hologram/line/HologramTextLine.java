package gg.manny.streamline.hologram.line;

import gg.manny.streamline.util.PlayerUtils;
import gg.manny.streamline.util.ReflectionUtils;
import gg.manny.streamline.util.entity.EntityUtils;
import gg.manny.streamline.util.entity.metadata.EntityMetadata;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static gg.manny.streamline.util.entity.metadata.EntityMetadata.*;

public class HologramTextLine extends HologramLine {

    @NonNull @Getter private String text;

    public HologramTextLine(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
        dataWatcher.watch(CUSTOM_NAME.getId(), this.text);
        dataWatcher.watch(CUSTOM_NAME_VISIBLE.getId(), (byte) 1);
    }

    @Override
    public List<Packet<?>> getPacketsFor(Player player, Location location) {
        List<Packet<?>> packets = new ArrayList<>();
        if (text.isEmpty() || text.equals(" ")) return packets;
        PacketPlayOutSpawnEntityLiving spawnPacket = getSpawnPacket(location);
        boolean legacy = PlayerUtils.onLegacyVersion(player);
        if (legacy) {
            setAsLegacyPacket(spawnPacket, location); // Sets as horse
            packets.add(getSkullPacket(location, OFFSET_HORSE));
            packets.add(new PacketPlayOutAttachEntity(armorStandId, skullId, false));
        }
        packets.add(0, spawnPacket);
        return packets;
    }

    public void updateText(Player player, String text) {
        this.text = text;
        DataWatcher dataWatcher = EntityUtils.getDataWatcher();
        dataWatcher.a(CUSTOM_NAME.getId(), text);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStandId, dataWatcher, true);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    private void setAsLegacyPacket(PacketPlayOutSpawnEntityLiving packet, Location location) {
        DataWatcher dataWatcher = EntityUtils.getDataWatcher();
        dataWatcher.a(EntityMetadata.INVISIBLE.getId(), (byte) 0);
        dataWatcher.a(1, (short) 300); // Not sure
        dataWatcher.a(CUSTOM_NAME.getId(), text);
        dataWatcher.a(CUSTOM_NAME_VISIBLE.getId(), (byte) 1);

        // This will make the Horse invisible, although it does not have
        // a collision box and the location won't be accurate
        // Which is why we use a Wither Projectile to ride the Horse (prevent it from falling)
        // and shouldn't be visible
        dataWatcher.a(AGE.getId(), -1700000);
        try {
            ReflectionUtils.setValue(packet, true, "b", (int) EntityType.HORSE.getTypeId());
            ReflectionUtils.setValue(packet, true, "c", MathHelper.floor((location.getY() - 0.13 + OFFSET_HORSE) * 32.0D)); // Update yPos for horse
            ReflectionUtils.setValue(packet, true, "l", dataWatcher);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void setText(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
    }
}