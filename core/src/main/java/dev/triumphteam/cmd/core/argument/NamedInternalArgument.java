package dev.triumphteam.cmd.core.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class NamedInternalArgument<S> extends LimitlessInternalArgument<S> {

    public NamedInternalArgument(
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final Class<?> type,
            final int position,
            final boolean isOptional
    ) {
        super(name, description, type, position, isOptional);
    }

    @Nullable
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final List<String> value) {
        return null;
    }
}
