package gg.manny.streamline.command.command;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DrinkHelpService {

    private final DrinkCommandService commandService;
    private HelpFormatter helpFormatter;

    public DrinkHelpService(DrinkCommandService commandService) {
        this.commandService = commandService;
        this.helpFormatter = (sender, container) -> {

            List<TextComponent> commands = new ArrayList<>();
            for (DrinkCommand c : container.getCommands().values()) {
                if (commandService.getAuthorizer().isAuthorized(sender, c, false)) {
                    String usage = c.getMostApplicableUsage().trim();
                    TextComponent msg = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                            "&e - /" + container.getName().toLowerCase() + (c.getName().length() > 0 ? " &d" + c.getName() : "") + (usage.isEmpty() ? "" : " &d" + usage) + " &7(" + c.getShortDescription() + "&7)"));
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "/" + container.getName().toLowerCase() + " " + c.getName() + " - " + ChatColor.WHITE + c.getDescription())));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + container.getName() + " " + c.getName()));
                    commands.add(msg);
                }
            }
            if (commands.isEmpty()) {
                sender.sendMessage(commandService.getAuthorizer().getNoPermissionMessage());
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + container.getName() + " Commands (&d" + container.getCommands().size() + "&e):"));
                commands.forEach(sender::sendMessage);
                sender.sendMessage(" ");
            }
        };
    }

    public void sendHelpFor(CommandSender sender, DrinkCommandContainer container) {
        this.helpFormatter.sendHelpFor(sender, container);
    }

    public void sendUsageMessage(CommandSender sender, DrinkCommandContainer container, DrinkCommand command) {
        sender.sendMessage(getUsageMessage(container, command));
    }

    public String getUsageMessage(DrinkCommandContainer container, DrinkCommand command) {
        String usage = ChatColor.RED + "Usage: /" + container.getName().toLowerCase() + " ";
        if (command.getName().length() > 0) {
            usage += command.getName() + " ";
        }
        if (command.getUsage() != null && command.getUsage().length() > 0) {
            usage += command.getUsage();
        } else {
            usage += command.getGeneratedUsage();
        }
        return usage;
    }

}
