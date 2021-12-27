package gg.manny.streamline.hologram;

import gg.manny.streamline.hologram.line.HologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Hologram {

    /**
     * Returns the identifier of a hologram
     * @return Unique identifier of the hologram
     */
    String getId();

    /**
     * Returns the location of a hologram
     * @return Location of hologram
     */
    Location getLocation();

    void setLocation(Location location);

    /**
     * Sends a hologram to viewers
     */
    void send();

    void destroy();

    void sendTo(Player player);

    void destroy(Player player);

    void update();

    void update(Player player);

    /**
     * Add lines to a hologram
     * @param lines Lines to add
     */
    void addLines(String... lines);

    /**
     * Set a specific line in a hologram
     * @param id Line Index
     * @param line Line to change
     */
    void setLine(int id, String line);

    void addItem(ItemStack item);

    void setItem(int id, ItemStack item);

    List<HologramLine> getLines();

    List<String> getTextLines();

    Set<UUID> getViewers();

}
