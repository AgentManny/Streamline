package gg.manny.streamline.command.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

import java.util.Set;

public class DrinkHelpTopic extends HelpTopic {

    private DrinkCommand command;

    public DrinkHelpTopic(DrinkCommand command, Set<String> aliases) {
        this.command = command;
        this.name = "/" + command.getName().toLowerCase();
        this.shortText = command.getShortDescription();
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GOLD);
        sb.append("Description: ");
        sb.append(ChatColor.WHITE);
        sb.append(command.getDescription());
        sb.append("\n");
        sb.append(ChatColor.GOLD);
        sb.append("Usage: ");
        sb.append(ChatColor.WHITE);
        sb.append(command.getMostApplicableUsage());
        if (aliases != null && aliases.size() > 0) {
            sb.append("\n");
            sb.append(ChatColor.GOLD);
            sb.append("Aliases: ");
            sb.append(ChatColor.WHITE);
            sb.append(StringUtils.join(aliases, ", "));
        }
        this.fullText = sb.toString();
    }

    public boolean canSee(CommandSender commandSender) {
        return commandSender.isOp() || commandSender.hasPermission(command.getPermission());
    }
}
