package gg.manny.streamline.command.provider.spigot;

import gg.manny.streamline.command.argument.CommandArg;
import gg.manny.streamline.command.exception.CommandExitMessage;
import gg.manny.streamline.command.parametric.DrinkProvider;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class CommandSenderProvider extends DrinkProvider<CommandSender> {

    public static final gg.manny.streamline.command.provider.spigot.CommandSenderProvider INSTANCE = new gg.manny.streamline.command.provider.spigot.CommandSenderProvider();

    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean allowNullArgument() {
        return true;
    }

    @Nullable
    @Override
    public CommandSender defaultNullValue() {
        return null;
    }

    @Override
    @Nullable
    public CommandSender provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        return arg.getSender();
    }

    @Override
    public String argumentDescription() {
        return "sender";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
