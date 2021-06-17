package dev.triumphteam.core.argument;

import org.jetbrains.annotations.NotNull;

public final class BasicArgument implements Argument {

    private final Class<?> type;
    private final ArgumentResolver resolver;

    public BasicArgument(@NotNull final Class<?> type, @NotNull final ArgumentResolver resolver) {
        this.type = type;
        this.resolver = resolver;
    }

    @Override
    public Object resolve(final Object value) {

        return "resolved";
    }
}
