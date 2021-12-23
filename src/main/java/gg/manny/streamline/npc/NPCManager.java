package gg.manny.streamline.npc;

import gg.manny.streamline.Streamline;
import gg.manny.streamline.npc.command.NPCCommand;

import java.util.HashSet;
import java.util.Set;

public class NPCManager {

    private final Streamline plugin;

    public Set<NPC> registeredNPCs = new HashSet<>(); // Entity Id, NPC

    public NPCManager(Streamline plugin) {
        this.plugin = plugin;
        Streamline.getCommandService(plugin)
                .register(new NPCCommand(this), "NPC");
    }
}
