package dev.triumphteam.core.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ArgumentResolver {

    @Nullable
    Object resolve(@NotNull final Object arg);

}
