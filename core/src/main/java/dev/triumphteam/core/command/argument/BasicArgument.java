package dev.triumphteam.core.command.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicArgument<S> implements Argument<S> {

    private final Class<?> type;
    private final ArgumentResolver<S> resolver;

    public BasicArgument(@NotNull final Class<?> type, @NotNull final ArgumentResolver<S> resolver) {
        this.type = type;
        this.resolver = resolver;
    }

    @NotNull
    @Override
    public Class<?> getType() {
        return type;
    }

    @Nullable
    @Override
    public Object resolve(@NotNull S sender, @NotNull final Object value) {
        return resolver.resolve(sender, value);
    }
    
}
