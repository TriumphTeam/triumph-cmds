package dev.triumphteam.core.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Argument {

    @Nullable
    Object resolve(@NotNull final Object value);

}
