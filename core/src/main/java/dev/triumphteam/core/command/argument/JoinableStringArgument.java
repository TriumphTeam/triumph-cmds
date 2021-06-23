package dev.triumphteam.core.command.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class JoinableStringArgument<S> implements LimitlessArgument<S> {

    private final CharSequence delimiter;

    public JoinableStringArgument(@NotNull final CharSequence delimiter) {
        this.delimiter = delimiter;
    }

    @NotNull
    @Override
    public Class<?> getType() {
        return String.class;
    }

    @Nullable
    @Override
    public Object resolve(@NotNull S sender, @NotNull final Object value) {
        if (!(value instanceof List)) return null;
        final List<String> list = (List<String>) value;
        if (list.isEmpty()) return null;
        return String.join(delimiter, list);
    }

}
