package dev.triumphteam.core.command.argument;

import dev.triumphteam.core.command.flag.Flags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FlagArgument<S> implements LimitlessArgument<S> {

    @NotNull
    @Override
    public Class<?> getType() {
        return Flags.class;
    }

    @Override
    public boolean isOptional() {
        // TODO check a better way for this
        return true;
    }

    @Nullable
    @Override
    public Object resolve(@NotNull S sender, @NotNull final Object value) {
        // TODO resolve
        return null;
    }

}
