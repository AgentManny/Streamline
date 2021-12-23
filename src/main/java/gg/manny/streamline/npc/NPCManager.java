package gg.manny.streamline.npc;

import gg.manny.streamline.Streamline;
import gg.manny.streamline.npc.command.NPCCommand;
import gg.manny.streamline.npc.event.NPCInteractEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class NPCManager implements Listener {

    private final Streamline plugin;

    private Map<Integer, NPC> npcs = new HashMap<>();

    public NPCManager(Streamline plugin) {
        this.plugin = plugin;
        Streamline.getCommandService(plugin)
                .register(new NPCCommand(this), "NPC");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void check(Player player, Location location, boolean loggedIn) {
        for (NPC npc : NPCRegistry.npcs) {
            Location npcLoc = npc.getLocation();
            if (npc.getLocation().getWorld().equals(player.getWorld())) {
                double x = Math.abs(location.getX() - npcLoc.getX());
                double z = Math.abs(location.getZ() - npcLoc.getZ());
                boolean visibility = x < NPC.VISIBILITY_RADIUS && z < NPC.VISIBILITY_RADIUS;
                if (!npc.getObservers().isEmpty() && npc.getObservers().contains(player.getUniqueId())) {
                    if (!visibility) {
                        npc.removeObserver(player);
                        return;
                    }
                    if (x < NPC.LOOK_RADIUS && z < NPC.LOOK_RADIUS) {
                        if (npc.getLooking().contains(player.getUniqueId())) {
                            Location clone = npcLoc.clone();
                            clone.setDirection(location.toVector().subtract(npcLoc.toVector()));
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
                    npc.addObserver(player, loggedIn);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        check(player, player.getLocation(), true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // We don't need to send a destroy packet since the entity doesn't exist
        for (NPC npc : NPCRegistry.npcs) {
            if (npc.getObservers().contains(player.getUniqueId())) {
                npc.getObservers().removeIf(id -> player.getUniqueId().equals(id));
            }
        }
    }

    @EventHandler
    public void onPlayerWorldSwitch(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = event.getWorld();
        for (NPC npc : NPCRegistry.npcs) {
            boolean sameWorld = npc.getLocation().getWorld().equals(world);
            if (npc.getObservers().contains(player.getUniqueId()) && !sameWorld) {
                npc.removeObserver(player);
            } else if (sameWorld) {
                check(player, player.getLocation(), false);
            }
        }
    }

    @EventHandler
    public void onNPCInteract(NPCInteractEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("You interacted with: " + event.getNpc().getName() + " (" + event.getInteract().name() + ")");
    }
}
