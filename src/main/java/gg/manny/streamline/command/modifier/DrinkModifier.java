package gg.manny.streamline.command.modifier;

import gg.manny.streamline.command.command.CommandExecution;
import gg.manny.streamline.command.exception.CommandExitMessage;
import gg.manny.streamline.command.parametric.CommandParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface DrinkModifier<T> {

    Optional<T> modify(@Nonnull CommandExecution execution, @Nonnull CommandParameter commandParameter, @Nullable T argument) throws CommandExitMessage;

}
