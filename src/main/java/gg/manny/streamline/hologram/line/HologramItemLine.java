package gg.manny.streamline.hologram.line;

import gg.manny.streamline.util.PlayerUtils;
import gg.manny.streamline.util.entity.dummy.DummyEntityItem;
import gg.manny.streamline.util.entity.metadata.EntityMetadata;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HologramItemLine extends HologramLine {

    @NonNull @Getter @Setter private ItemStack item;

    @Getter private final int itemId;
    private final DataWatcher itemData;

    public HologramItemLine(ItemStack itemStack) {
        this.item = itemStack;

        WorldServer worldServer = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        DummyEntityItem item = new DummyEntityItem(worldServer, itemStack);
        itemId = item.getId();
        itemData = item.getDataWatcher();
    }

    @Override
    public List<Packet<?>> getPacketsFor(Player player, Location location) {
        List<Packet<?>> packets = new ArrayList<>();
        boolean legacy = PlayerUtils.onLegacyVersion(player);
        packets.add(getItemPacket(location));
        packets.add(getUpdateItemPacket(item));
        packets.add(legacy ? getSkullPacket(location, 0) : getSpawnPacket(location));
        packets.add(new PacketPlayOutAttachEntity(itemId, legacy ? skullId : armorStandId, false));
        return packets;
    }

    private PacketPlayOutSpawnEntity getItemPacket(Location location) {
        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(itemId,
                location.getX(),
                location.getY() - 0.13 + HologramLine.OFFSET_OTHER,
                location.getZ(),
                0, 0, 0,
                (int) location.getPitch(),
                (int) location.getYaw(),
                HologramLine.ITEM_STACK, 0
        );
        return spawnPacket;
    }

    private PacketPlayOutEntityMetadata getUpdateItemPacket(ItemStack item) {
        itemData.watch(EntityMetadata.ITEM.getId(), CraftItemStack.asNMSCopy(item));
        itemData.update(EntityMetadata.ITEM.getId());
        return new PacketPlayOutEntityMetadata(itemId, itemData, true);
    }

    public void update(Player player, ItemStack item) {
        PacketPlayOutEntityMetadata packet = getUpdateItemPacket(item);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    // TODO Send destroy packet
    public void destroy() {

    }

    // TODO send update metadata packet
    public void update() {

    }

    // TODO send teleport packet
    public void teleport(Location location) {
//        new PacketPlayOutEntityTeleport()
    }

    public void setLocation(Location location) {
//        this.location = location;
        teleport(location);
    }
}
