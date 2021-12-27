package gg.manny.streamline.npc;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NPCRegistry {

    @Getter public static final Set<NPC> npcs = new HashSet<>();

    @Getter public static final Map<Integer, NPC> storedNPCs = new HashMap<>(); // These are NPCs created and serialized internally

    public static void add(NPC npc) {
        npcs.add(npc);
    }

    public static void register(Integer id, NPC npc) {
        if (storedNPCs.containsKey(id)) {
            NPC remove = storedNPCs.remove(id);
            if (remove != null) {
                remove.destroy();
            }
        }
        storedNPCs.put(id, npc);
    }
}
