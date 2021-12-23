package gg.manny.streamline.command.exception;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandExitMessage extends Exception {

    public CommandExitMessage(String message) {
        super(message);
    }

    public void print(CommandSender sender) {
        String message = getMessage()
                .replaceFirst("'", ChatColor.YELLOW.toString())
                        .replaceFirst("'", ChatColor.RED.toString());
        sender.sendMessage(ChatColor.RED + "Error: " + message);
    }
}
