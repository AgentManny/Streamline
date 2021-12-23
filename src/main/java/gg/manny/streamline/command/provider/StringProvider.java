package gg.manny.streamline.command.provider;

import gg.manny.streamline.command.argument.CommandArg;
import gg.manny.streamline.command.exception.CommandExitMessage;
import gg.manny.streamline.command.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class StringProvider extends DrinkProvider<String> {

    public static final gg.manny.streamline.command.provider.StringProvider INSTANCE = new gg.manny.streamline.command.provider.StringProvider();

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
        return true;
    }

    @Nullable
    @Override
    public String defaultNullValue() {
        return null;
    }

    @Override
    public String provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        return arg.get();
    }

    @Override
    public String argumentDescription() {
        return "string";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
