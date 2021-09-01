package dev.triumphteam.core.command.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicArgument<S> implements Argument<S> {

    private final Class<?> type;
    private final ArgumentResolver<S> resolver;
    private final boolean optional;

    public BasicArgument(
            @NotNull final Class<?> type,
            @NotNull final ArgumentResolver<S> resolver,
            final boolean optional
    ) {
        this.type = type;
        this.resolver = resolver;
        this.optional = optional;
    }

    @NotNull
    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Nullable
    @Override
    public Object resolve(@NotNull S sender, @NotNull final Object value) {
        if (!(value instanceof String)) return null;
        return resolver.resolve(sender, (String) value);
    }

    @Override
    public String toString() {
        return "BasicArgument{" +
                "type=" + type +
                '}';
    }

}
