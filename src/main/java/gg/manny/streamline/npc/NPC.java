package gg.manny.streamline.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gg.manny.streamline.Streamline;
import gg.manny.streamline.npc.event.NPCInteractEvent;
import gg.manny.streamline.util.PlayerUtils;
import gg.manny.streamline.util.ReflectionUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Skin;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class NPC {

    public final static int LOOK_RADIUS = 5;
    public final static int VISIBILITY_RADIUS = 25;

    /** Returns unique identifier of NPC */
    private final UUID id;

    /** Returns display name of NPC */
    private final String name;

    /** Returns the entity id of NPC */
    public final int entityId;

    private ItemStack heldItem;

    private Location location;

    /** Returns the skin of NPC */
    private Skin skin;

    /** Returns the game profile of NPC */
    private GameProfile profile;

    /** Returns whether an NPC should look at a player */
    @Setter private boolean look = false;
    private Set<UUID> looking = new HashSet<>();

    private Set<UUID> observers = new HashSet<>();

    @Setter private long interactionDelay = 90L;
    private Map<UUID, Long> lastInteraction = new WeakHashMap<>();

    public NPC(String name, Skin skin, Location location) {
        this.name = name;
        this.id = UUID.randomUUID();
        this.entityId = Streamline.getInstance().getServer().allocateEntityId();

        this.profile = new GameProfile(id, name);
        setSkin(skin); // Sets game profile

        this.location = location;
    }

    private List<Packet<?>> getSpawnPacket(boolean legacy) {
        List<Packet<?>> packets = new ArrayList<>();

        DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, (byte) 0);
        watcher.a(1, (short) 0);
        watcher.a(8, (byte) 0);

        PacketPlayOutNamedEntitySpawn spawnEntity = new PacketPlayOutNamedEntitySpawn(
                entityId, id,
                location.getX(), location.getY(), location.getZ(), (byte) location.getYaw(), (byte) location.getPitch(),
                heldItem,
                watcher
        );

        packets.add(spawnEntity);
        if (!legacy) { // Players won't appear without this packet
            PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            playerInfo.b.add(playerInfo.constructData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, new ChatComponentText(name)));
            packets.add(0, playerInfo);
        }
        return packets;
    }

    public void teleport(Location location) {
        int x = MathHelper.floor(location.getX() * 32.0D);
        int y = MathHelper.floor(location.getY() * 32.0D);
        int z = MathHelper.floor(location.getZ() * 32.0D);
        byte yaw = (byte) ((int)(location.getYaw() * 256.0F / 360.0F));
        byte pitch = (byte) -((int)(location.getPitch() * 256.0F / 360.0F));
        PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(
                entityId,
                x, y, z, yaw, pitch,
                true
        );
        sendPacket(observers, teleport, getRotationPacket(location.getYaw()));
    }

    public void interact(EntityPlayer player, PacketPlayInUseEntity useEntity) {
        PacketPlayInUseEntity.EnumEntityUseAction useAction = useEntity.a();
        Interact interact = useAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK ? Interact.LEFT_CLICK : Interact.RIGHT_CLICK;
        boolean throttled = System.currentTimeMillis() - lastInteraction.getOrDefault(player.getUniqueID(), 0L) <= interactionDelay;
        if (throttled) return;
        lastInteraction.put(player.getUniqueID(), System.currentTimeMillis());
        Streamline.getInstance().getServer().getPluginManager().callEvent(new NPCInteractEvent(player.getBukkitEntity(), this, interact));
    }

    private PacketPlayOutEntityHeadRotation getRotationPacket(float yaw) {
        PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation();
        try {
            ReflectionUtils.setValue(headRotation, true, "a", entityId);
            ReflectionUtils.setValue(headRotation, true, "b", (byte) ((int)(yaw * 256.0F / 360.0F)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headRotation;
    }

    public void setLook(Player player, float yaw, float pitch) {
        PacketPlayOutEntity.PacketPlayOutEntityLook entityLook = new PacketPlayOutEntity.PacketPlayOutEntityLook(getEntityId(), (byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false);
        PacketPlayOutEntityHeadRotation rotationPacket = getRotationPacket(yaw);
        PlayerUtils.sendPacket(player, entityLook);
        PlayerUtils.sendPacket(player, rotationPacket);
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
        this.profile.getProperties().put("textures", new Property("textures", skin.getData(), skin.getSignature()));
    }

    /**
     * Sends a destroy packet to all observers and re-spawns the NPC
     */
    public void update() {
        for (UUID observer : observers) {
            Player player = Bukkit.getPlayer(observer);
            if (observer != null) {
                remove(player);
                Streamline.getInstance().getServer().getScheduler().runTaskLater(Streamline.getInstance(),
                        () -> create(player), 4L);
            }
        }
    }

    public void addObserver(Player player) {
        if (!observers.contains(player.getUniqueId())) {
            observers.add(player.getUniqueId());
            create(player);
        }
    }

    public void removeObserver(Player player) {
        if (observers.remove(player.getUniqueId())) {
            remove(player);
            lastInteraction.remove(player.getUniqueId());
        }
    }

    public void create(Player player) {
        boolean legacy = PlayerUtils.onLegacyVersion(player);
        getSpawnPacket(legacy).forEach(packet -> PlayerUtils.sendPacket(player, packet));
    }

    public void remove(Player player) {
        boolean legacy = PlayerUtils.onLegacyVersion(player);
        if (!legacy) {
            PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
            playerInfo.b.add(playerInfo.constructData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, new ChatComponentText(name)));
            PlayerUtils.sendPacket(player, playerInfo);
        }
        PlayerUtils.sendPacket(player, new PacketPlayOutEntityDestroy(entityId));
    }

    public void destroy() {
        for (UUID observer : observers) {
            Player player = Bukkit.getPlayer(observer);
            if (observer != null) {
                remove(player);
            }
        }
        observers.clear();
        lastInteraction.clear();
    }

    private static void sendPacket(Set<UUID> players, Packet... packets) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                for (Packet packet : packets) {
                    PlayerUtils.sendPacket(player, packet);
                }
            }
        }
    }

    public enum Interact {

        RIGHT_CLICK,
        LEFT_CLICK

    }
}