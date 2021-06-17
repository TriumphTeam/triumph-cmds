package dev.triumphteam.core.command.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ArgumentResolver<S> {

    @Nullable
    Object resolve(@NotNull S sender, @NotNull final Object arg);

}
