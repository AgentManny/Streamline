package gg.manny.streamline.npc.event;

import gg.manny.streamline.npc.NPC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class NPCInteractEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private NPC npc;
    private NPC.Interact interact;

    public NPCInteractEvent(Player who, NPC npc, NPC.Interact interact) {
        super(who);

        this.npc = npc;
        this.interact = interact;
    }

    public boolean isLeftClicked() {
        return interact == NPC.Interact.LEFT_CLICK;
    }

    public boolean isRightClicked() {
        return interact == NPC.Interact.RIGHT_CLICK;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
