package gg.manny.streamline.command.provider;

import gg.manny.streamline.command.argument.CommandArg;
import gg.manny.streamline.command.exception.CommandExitMessage;
import gg.manny.streamline.command.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class IntegerProvider extends DrinkProvider<Integer> {

    public static final gg.manny.streamline.command.provider.IntegerProvider INSTANCE = new gg.manny.streamline.command.provider.IntegerProvider();

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Nullable
    @Override
    public Integer defaultNullValue() {
        return 0;
    }

    @Override
    @Nullable
    public Integer provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String s = arg.get();
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            throw new CommandExitMessage("Required: Integer, Given: '" + s + "'");
        }
    }

    @Override
    public String argumentDescription() {
        return "integer";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
