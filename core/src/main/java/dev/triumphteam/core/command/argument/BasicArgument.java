package dev.triumphteam.core.command.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicArgument<S> implements Argument<S> {

    private final ArgumentResolver<S> resolver;

    public BasicArgument(@NotNull final ArgumentResolver<S> resolver) {
        this.resolver = resolver;
    }

    @Nullable
    @Override
    public Object resolve(@NotNull S sender, @NotNull final Object value) {
        return resolver.resolve(sender, value);
    }
}
