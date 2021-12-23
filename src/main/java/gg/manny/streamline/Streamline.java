package gg.manny.streamline;

import com.google.common.base.Preconditions;
import gg.manny.streamline.command.CommandService;
import gg.manny.streamline.command.command.DrinkCommandService;
import gg.manny.streamline.handler.ServerMovementHandler;
import gg.manny.streamline.handler.ServerPacketHandler;
import gg.manny.streamline.menu.MenuHandler;
import gg.manny.streamline.npc.NPCManager;
import gg.manny.streamline.npc.event.NPCInteractEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import rip.thecraft.server.CraftServer;
import rip.thecraft.server.handler.MovementHandler;
import rip.thecraft.server.handler.PacketHandler;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public class Streamline extends JavaPlugin implements Listener {

    private static final ConcurrentMap<String, CommandService> services = new ConcurrentHashMap<>();

    @Getter private static Streamline instance;

    private boolean protocolSupport = false;

    private NPCManager npcManager;

    // Temp
    private PacketHandler packetHandler;
    private MovementHandler movementHandler;

    @Override
    public void onEnable() {
        instance = this;

        protocolSupport = getServer().getPluginManager().isPluginEnabled("ProtocolSupport");

        CommandService commandService = getCommandService(this);

        npcManager = new NPCManager(this);

        MenuHandler.init(this);

        CraftServer.getInstance().addPacketHandler(packetHandler = new ServerPacketHandler(this, npcManager));
        CraftServer.getInstance().addMovementHandler(movementHandler = new ServerMovementHandler(this, npcManager));
        getServer().getPluginManager().registerEvents(this, this);

        commandService.registerCommands();
    }

    @Override
    public void onDisable() {
        CraftServer.getInstance().getPacketHandlers().remove(packetHandler);
        CraftServer.getInstance().getMovementHandlers().remove(movementHandler);
    }

    /**
     * Get a {@link CommandService} instance to register commands via
     * - JavaPlugin specific (one per plugin instance)
     *
     * @param javaPlugin {@link Nonnull} your {@link JavaPlugin} instance
     * @return The {@link CommandService} instance
     */
    public static CommandService getCommandService(@Nonnull JavaPlugin javaPlugin) {
        Preconditions.checkNotNull(javaPlugin, "JavaPlugin cannot be null");
        return services.computeIfAbsent(javaPlugin.getName(), name -> new DrinkCommandService(javaPlugin));
    }
}