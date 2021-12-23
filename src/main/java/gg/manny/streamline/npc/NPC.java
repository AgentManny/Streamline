package gg.manny.streamline.npc;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import gg.manny.streamline.Streamline;
import gg.manny.streamline.npc.event.NPCInteractEvent;
import gg.manny.streamline.util.PlayerUtils;
import gg.manny.streamline.util.ReflectionUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Skin;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.util.Skins;
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
        Skins.setProperties(this.skin = skin, profile.getProperties());

        this.location = location;
    }

    public NPC(JsonObject data) {
        this.id = UUID.fromString(data.get("id").getAsString());
        this.name = data.get("name").getAsString();
        this.entityId = Streamline.getInstance().getServer().allocateEntityId();

        this.profile = new GameProfile(id, name);

        JsonObject skinData = data.get("skin").getAsJsonObject();
        Skin skin = new Skin(skinData.get("texture").getAsString(), skinData.get("signature").getAsString());
        Skins.setProperties(this.skin = skin, profile.getProperties());

        JsonObject locationData = data.get("location").getAsJsonObject();
        World world = Bukkit.getWorld(locationData.get("world").getAsString());
        double x = locationData.get("x").getAsDouble();
        double y = locationData.get("y").getAsDouble();
        double z = locationData.get("z").getAsDouble();
        float yaw = locationData.get("yaw").getAsFloat();
        float pitch = locationData.get("pitch").getAsFloat();
        this.location = new Location(world, x, y, z, yaw, pitch);
    }

    public JsonObject serialize() {
        JsonObject data = new JsonObject();
        data.addProperty("id", id.toString());
        data.addProperty("name", name);

        JsonObject skinData = new JsonObject();
        skinData.addProperty("texture", skin.getData());
        skinData.addProperty("signature", skin.getSignature());
        data.add("skin", skinData);

        JsonObject locationData = new JsonObject();
        locationData.addProperty("world", location.getWorld().getName());
        locationData.addProperty("x", location.getX());
        locationData.addProperty("y", location.getY());
        locationData.addProperty("z", location.getZ());
        locationData.addProperty("yaw", location.getYaw());
        locationData.addProperty("pitch", location.getPitch());
        data.add("location", locationData);
        return data;
    }

    private List<Packet<?>> getSpawnPacket(boolean legacy) {
        List<Packet<?>> packets = new ArrayList<>();

        DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, (byte) 0);
        watcher.a(1, (short) 0);
        watcher.a(8, (byte) 0);
        watcher.a(10, (byte) 127); // Skin parts

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
        Skins.setProperties(this.skin = skin, profile.getProperties());
    }

    /**
     * Sends a destroy packet to all observers and re-spawns the NPC
     */
    public void update() {
        for (UUID observer : observers) {
            Player player = Bukkit.getPlayer(observer);
            if (observer != null) {
                remove(player);
                Streamline.getInstance().getServer().getScheduler().runTask(Streamline.getInstance(),
                        () -> {
                            create(player, false);
                            sendAnimation(AnimationType.SWING);
                        });
            }
        }
    }

    public void addObserver(Player player, boolean loggedIn) {
        if (!observers.contains(player.getUniqueId())) {
            observers.add(player.getUniqueId());
            create(player, loggedIn);
        }
    }

    public void removeObserver(Player player) {
        if (observers.remove(player.getUniqueId())) {
            remove(player);
            lastInteraction.remove(player.getUniqueId());
        }
    }

    public void create(Player player, boolean loggedIn) {
        boolean legacy = PlayerUtils.onLegacyVersion(player);
        getSpawnPacket(legacy).forEach(packet -> PlayerUtils.sendPacket(player, packet));
        if (!legacy) {
            Streamline.getInstance().getServer().getScheduler().runTaskLater(Streamline.getInstance(), () -> {
                if (observers.contains(player.getUniqueId())) {
                    Bukkit.broadcastMessage("Removing " + name + " from TAB as this player is visible");
                    PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
                    playerInfo.b.add(playerInfo.constructData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, new ChatComponentText(name)));
                    PlayerUtils.sendPacket(player, playerInfo);
                } else {
                    Bukkit.broadcastMessage("Test");
                }
            }, loggedIn ? 30L : 10L);
        }
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

    public void update(DataWatcher dataWatcher, boolean value) {
        sendPacket(observers, new PacketPlayOutEntityMetadata(entityId, dataWatcher, value));
    }

    public void sendAnimation(AnimationType animation) {
        PacketPlayOutAnimation armAnimation = new PacketPlayOutAnimation();
        try {
            ReflectionUtils.setValue(armAnimation, true, "a", entityId);
            ReflectionUtils.setValue(armAnimation, true, "b", animation.id);
            sendPacket(observers, armAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(int id, byte bitmask) {
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(id, bitmask);
        update(watcher, true);
    }

    public void sendStatus(PlayerStatus status, boolean value) {
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, (byte) status.bitmask);
        update(watcher, value);
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

    @AllArgsConstructor
    public enum PlayerStatus {

        FIRE(0x01), CROUCHING(0x02), SPRINTING(0x08), BLOCK(0x16), INVISIBLE(0x20),
        ;

        public int bitmask;
    }

    @AllArgsConstructor
    public enum AnimationType {

        SWING(0), DAMAGE(1), CRITICALS_PARTICLE(4), ENCHANTMENT(5), //    EAT(6),
        ;

        public int id;
    }
}