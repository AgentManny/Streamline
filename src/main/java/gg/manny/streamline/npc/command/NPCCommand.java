package gg.manny.streamline.npc.command;

import gg.manny.streamline.command.annotation.Command;
import gg.manny.streamline.command.annotation.Sender;
import gg.manny.streamline.command.annotation.Text;
import gg.manny.streamline.npc.NPC;
import gg.manny.streamline.npc.NPCManager;
import gg.manny.streamline.npc.NPCRegistry;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Skin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class NPCCommand {

    private final NPCManager npcManager;

    @Command(name = "list", desc = "List all NPCs")
    public void list(@Sender CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "NPCs (" + ChatColor.LIGHT_PURPLE + NPCRegistry.npcs.size() + ChatColor.YELLOW + "):");
        for (NPC npc : NPCRegistry.npcs) {
            sender.sendMessage(ChatColor.YELLOW + " - " + ChatColor.LIGHT_PURPLE + npc.getName() + ChatColor.YELLOW + " (" + npc.getLocation().toVector().toString() + ")");
        }
        sender.sendMessage(" ");
    }

    private NPC npc;

    @Command(name = "create", desc = "Create an NPC")
    public void create(@Sender Player sender, @Text String name) {
        npc = new NPC(name, sender.getSkin(), sender.getLocation());
        NPCRegistry.npcs.add(npc);
    }


    @Command(name = "skin", desc = "Create an NPC")
    public void skin(@Sender Player sender, Skin skin) {
        npc.setSkin(skin);
        npc.update();
    }

}
