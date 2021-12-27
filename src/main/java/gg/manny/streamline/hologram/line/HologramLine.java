package gg.manny.streamline.hologram.line;

import gg.manny.streamline.util.entity.dummy.DummyEntityArmorStand;
import gg.manny.streamline.util.entity.dummy.DummyEntityWitherSkull;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class HologramLine {

    public static final int ITEM_STACK = 2;
    protected static int ARMOR_STAND_ID = 30;
    protected static int WITHER_SKULL_PROJECTILE_ID = 66;

    protected static double OFFSET_HORSE = 56.5;
    protected static double OFFSET_OTHER = 1.5;

    protected int armorStandId;
    protected int skullId;

    protected DataWatcher dataWatcher; // For armor stands

    public HologramLine() {
        // TODO Generate entity id without creating an Entity constructor
        WorldServer worldServer = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        DummyEntityArmorStand entity = new DummyEntityArmorStand(worldServer);
        armorStandId = entity.getId();
        dataWatcher = entity.getDataWatcher();

        // I should remove Dummy entities and use reflections to add to the entity id
        // instead of wasting garbage collection resources since I'm only sending holograms through
        // packets, unless I plan to have global entities
        DummyEntityWitherSkull witherSkull = new DummyEntityWitherSkull(worldServer);
        skullId = witherSkull.getId();
    }

    public abstract List<Packet<?>> getPacketsFor(Player player, Location location);

    public PacketPlayOutSpawnEntityLiving getSpawnPacket(Location location) {
        return new PacketPlayOutSpawnEntityLiving(armorStandId, (byte) HologramLine.ARMOR_STAND_ID,
                location.getX(), (location.getY() - 0.29) + HologramLine.OFFSET_OTHER, location.getZ(),
                0, 0, 0,
                0, 0, 0,
                dataWatcher
        );
    }

    public PacketPlayOutSpawnEntity getSkullPacket(Location location, double offset) {
        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(skullId,
                location.getX(),
                location.getY() - 0.13 + offset,
                location.getZ(),
                0, 0, 0,
                (int) location.getPitch(),
                (int) location.getYaw(),
                WITHER_SKULL_PROJECTILE_ID, 0
        );
        return spawnPacket;
    }

    // TODO send update metadata packet
    public void update() {

    }

    // TODO send teleport packet
    public void teleport(Location location) {

    }

    public void setLocation(Location location) {
//        this.location = location;
        teleport(location);
    }
}