package gg.manny.streamline.hologram.command;


import gg.manny.streamline.command.annotation.Command;
import gg.manny.streamline.command.annotation.Require;
import gg.manny.streamline.command.annotation.Sender;
import gg.manny.streamline.command.annotation.Text;
import gg.manny.streamline.hologram.Hologram;
import gg.manny.streamline.hologram.HologramRegistry;
import gg.manny.streamline.hologram.line.HologramItemLine;
import gg.manny.streamline.hologram.line.HologramLine;
import gg.manny.streamline.hologram.line.HologramTextLine;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Require("hologram.command")
public class HologramCommand {

    @Command(name = "hologram additem", aliases = "holo additem", desc = "Adds the item in your hand as a hologram")
    public void addItem(@Sender Player player, Hologram hologram) {
        ItemStack item = player.getItemInHand();
        if (item == null) {
            player.sendMessage(ChatColor.RED + "You don't have an item in your hand.");
            return;
        }

        hologram.addItem(item);
        player.sendMessage(ChatColor.GOLD + "Set item " + ChatColor.WHITE + WordUtils.capitalizeFully(item.getType().name().toLowerCase().replace("_", " ")) + ChatColor.GOLD + " for " + ChatColor.RESET + hologram.getId() + ChatColor.GOLD + " hologram.");
    }

    @Command(name = "hologram addline", aliases = "holo addline", desc = "Adds a line to a hologram")
    public void addLine(@Sender Player player, Hologram hologram, @Text String line) {
        hologram.addLines(line);
        player.sendMessage(ChatColor.GOLD + "Added \"" + ChatColor.WHITE + line + ChatColor.GOLD + "\" line to: " + ChatColor.RESET + hologram.getId());
    }

    @Command(name = "hologram list", aliases = "holo list", desc = "List all holograms hologram")
    public void addLine(@Sender CommandSender sender) {
        Collection<Hologram> holograms = HologramRegistry.getHolograms().values();
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.GOLD + "Holograms (" + ChatColor.WHITE + holograms.size() + ChatColor.GOLD + "):");
        for (Hologram hologram : holograms) {
            List<String> lines = hologram.getTextLines();
            Location location = hologram.getLocation();
            sender.sendMessage(ChatColor.YELLOW + " - " + ChatColor.WHITE + hologram.getId() + ChatColor.GRAY +
                    " (Lines: " + ChatColor.WHITE + lines.size() + ChatColor.GRAY + ") " +
                    "(Location: " + ChatColor.WHITE + location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ChatColor.GRAY + ")");
        }
    }

    @Command(name = "hologram info", aliases = "holo info", desc = "Information on a hologram")
    public void info(@Sender CommandSender sender, Hologram hologram) {
        Location location = hologram.getLocation();
        List<HologramLine> lines = hologram.getLines();

        new FancyMessage(ChatColor.WHITE + hologram.getId() + ChatColor.GOLD + " Hologram" + ChatColor.GRAY + " (" + location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")")
                .suggest(ChatColor.YELLOW + "Click to teleport")
                .command("/tppos " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + location.getWorld().getName())
                .send(sender);
        sender.sendMessage(ChatColor.GRAY + "Viewers: " + ChatColor.WHITE + hologram.getViewers().stream()
                .map((id) -> {
                    Player viewer = Bukkit.getPlayer(id);
                    return viewer == null ? id.toString() : viewer.getName();
                })
                .collect(Collectors.joining(", ")));
        for (int i = 1; i <= lines.size(); i++) {
            HologramLine line = lines.get(i - 1);
            String formattedLine = line instanceof HologramTextLine ? ((HologramTextLine)line).getText() : line instanceof HologramItemLine ? WordUtils.capitalize(((HologramItemLine)line).getItem().getType().name().toLowerCase()) : "Unknown";
            new FancyMessage(ChatColor.GOLD.toString() + i + ". " + ChatColor.WHITE + formattedLine)
                    .tooltip(ChatColor.AQUA + line.getClass().getSimpleName())
                    .send(sender);
        }
    }

}
