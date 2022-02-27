package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

public interface Argument {

    static ArgumentBuilder forString() {
        return new ArgumentBuilder(String.class);
    }

    static ArgumentBuilder forInt() {
        return new ArgumentBuilder(Integer.class);
    }

    static ArgumentBuilder forFloat() {
        return new ArgumentBuilder(Float.class);
    }

    static ArgumentBuilder forDouble() {
        return new ArgumentBuilder(Double.class);
    }

    static ArgumentBuilder forBoolean() {
        return new ArgumentBuilder(boolean.class);
    }

    static ArgumentBuilder forType(@NotNull final Class<?> type) {
        return new ArgumentBuilder(type);
    }

    @NotNull
    Class<?> getType();

    @NotNull
    String getName();

    @NotNull
    String getDescription();
}
