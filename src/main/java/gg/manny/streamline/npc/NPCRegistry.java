package gg.manny.streamline.npc;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class NPCRegistry {

    @Getter public static final Set<NPC> npcs = new HashSet<>();

    public static void register(NPC npc) {
        npcs.add(npc);
    }
}
