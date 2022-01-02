package gg.manny.streamline.hologram.command.provider;

import gg.manny.streamline.command.argument.CommandArg;
import gg.manny.streamline.command.exception.CommandExitMessage;
import gg.manny.streamline.command.parametric.DrinkProvider;
import gg.manny.streamline.hologram.Hologram;
import gg.manny.streamline.hologram.HologramRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HologramProvider extends DrinkProvider<Hologram> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Override
    @Nullable
    public Hologram provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String s = arg.get();
        Optional<Hologram> hologram = HologramRegistry.getHologram(s);
        if (hologram.isPresent()) {
            return hologram.get();
        }
        throw new CommandExitMessage("Hologram '" + s + "' not found.");
    }

    @Override
    public String argumentDescription() {
        return "hologram";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
