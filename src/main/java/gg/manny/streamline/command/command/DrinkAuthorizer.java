package gg.manny.streamline.command.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

@Getter
@Setter
public class DrinkAuthorizer {

    private String noPermissionMessage = ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.";

    public boolean isAuthorized(@Nonnull CommandSender sender, @Nonnull DrinkCommand command, boolean notify) {
        String node = command.getPermission();
        if (node != null && node.length() > 0) {
            if (node.equalsIgnoreCase("op") && !sender.isOp()) {
                if (notify) {
                    sender.sendMessage(noPermissionMessage);
                }
                return false;
            }
            if (!sender.hasPermission(node)) {
                if (notify) {
                    sender.sendMessage(noPermissionMessage);
                }
                return false;
            }
        }
        return true;
    }

}
