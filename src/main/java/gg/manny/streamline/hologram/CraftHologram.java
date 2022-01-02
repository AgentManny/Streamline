package gg.manny.streamline.hologram;

import com.google.gson.JsonObject;
import gg.manny.streamline.hologram.line.HologramItemLine;
import gg.manny.streamline.hologram.line.HologramLine;
import gg.manny.streamline.hologram.line.HologramTextLine;
import gg.manny.streamline.util.LocationSerializer;
import gg.manny.streamline.util.PlayerUtils;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class CraftHologram implements Hologram {

    public static int DISTANCE_RADIUS = 20;
    public static final double DISTANCE = 0.23; // Credits: filoghost

    private String id;

    protected List<HologramLine> lastLines = new ArrayList<>();
    public List<HologramLine> lines = new ArrayList<>();

    private Set<UUID> viewers = new ConcurrentSet<>(); // todo Change to concurrent? It'll be removing adding a lot

    @NonNull private Location location;

    public CraftHologram(JsonObject data) {
        this.id = data.get("id").getAsString();
        this.location = LocationSerializer.fromString(data.get("location").getAsString());
    }

    public CraftHologram(String id, Location location) {
        this.id = id;
        this.location = location;
    }

    public CraftHologram(Location location) {
        this.location = location;
    }

    @Override
    public void send() {
        if (viewers.isEmpty()) { // Assumes public hologram
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player -> !viewers.contains(player.getUniqueId()) && player.getLocation().distance(location) < DISTANCE_RADIUS)
                    .forEach(this::sendTo);
        } else {
            for (UUID viewer : viewers) {
                Player player = Bukkit.getPlayer(viewer);
                if (player != null) {
                    sendTo(player);
                }
            }
        }
    }

    @Override
    public void sendTo(Player player) {
        viewers.add(player.getUniqueId());
        Location location = this.location.clone().add(0, this.lines.size() * DISTANCE, 0);
        for (HologramLine line : lines) {
            if (line instanceof HologramItemLine) {
                location.subtract(0, DISTANCE / 2, 0);
            }
            line.getPacketsFor(player, location).forEach(packet -> sendPacket(player, packet));
            location.subtract(0, line instanceof HologramItemLine ? DISTANCE * 2 : DISTANCE, 0);
        }
    }

    @Override
    public void destroy(Player player) {
        if (viewers.contains(player.getUniqueId())) {
            List<Integer> entityIds = new ArrayList<>();
            boolean legacy = PlayerUtils.onLegacyVersion(player);
            for (HologramLine line : lines) {
                entityIds.add(line.getArmorStandId());
                if (legacy) {
                    entityIds.add(line.getSkullId());
                }
                if (line instanceof HologramItemLine) {
                    entityIds.add(((HologramItemLine) line).getItemId());
                }
            }
            int[] ids = entityIds.stream().mapToInt(i -> i).toArray();
            sendPacket(player, new PacketPlayOutEntityDestroy(ids));
            viewers.remove(player.getUniqueId());
        }
    }

    @Override
    public void destroy() {
        for (UUID viewer : viewers) {
            Player player = Bukkit.getPlayer(viewer);
            if (player != null) {
                destroy(player);
            }
        }
    }

    @Override
    public void update() {
        for (UUID viewer : viewers) {
            Player player = Bukkit.getPlayer(viewer);
            if (player != null) {
                update(player);
            }
        }
        lastLines = new ArrayList<>(lines);
    }

    @Override
    public void update(Player player) {
        if (!player.getLocation().getWorld().equals(location.getWorld())) return;

        if (lastLines.size() != lines.size()) {
            destroy(player);
            sendTo(player);
            return;
        }

        for (int i = 0; i < lines.size(); i++) {
            HologramLine line = lines.get(i);
            HologramLine lastLine = lastLines.get(i);

            if (!lastLine.getClass().equals(line.getClass())) { // TODO Find a way to replace individual line instead
                destroy(player);
                sendTo(player);
                return;
            }

            if (line instanceof HologramTextLine) {
                HologramTextLine textLine = (HologramTextLine) line;
                textLine.updateText(player, textLine.getText());
            } else if (line instanceof HologramItemLine) {
                HologramItemLine itemLine = (HologramItemLine) line;
                itemLine.update(player, itemLine.getItem());
            }
        }
    }

    @Override
    public void addLines(String... lines) {
        for (String line : lines) {
            this.lines.add(new HologramTextLine(line));
        }
        update();
    }

    @Override
    public void setLine(int id, String line) {
        if (id > lines.size() - 1) {
            lines.add(new HologramTextLine(line));
        } else if (lines.get(id) instanceof HologramTextLine) {
            ((HologramTextLine) lines.get(id)).setText(line);
        } else {
            lines.set(id, new HologramTextLine(line));
        }
        update();
    }

    @Override
    public void addItem(ItemStack item) {
        this.lines.add(new HologramItemLine(item));
        update();
    }

    @Override
    public void setItem(int id, ItemStack item) {
        if (id > lines.size() - 1) {
            lines.add(new HologramItemLine(item));
        } else if (lines.get(id) instanceof HologramItemLine) {
            ((HologramItemLine) lines.get(id)).setItem(item);
        } else {
            lines.set(id, new HologramItemLine(item));
        }
        update();
    }

    @Override
    public List<String> getTextLines() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < this.lines.size(); i++) {
            HologramLine line = this.lines.get(i);
            if (line instanceof HologramTextLine) {
                HologramTextLine textLine = (HologramTextLine) line;
                lines.add(textLine.getText());
            }
        }
        return lines;
    }

    @Override
    public void setLocation(Location location) {
        // todo Update location with teleport packet
        this.location = location;
        destroy();
        send();
    }

    private void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public JsonObject serialize() {
        JsonObject data = new JsonObject();
        data.addProperty("id", this.id);
        data.addProperty("location", LocationSerializer.toString(this.location));
        return data;
    }
}