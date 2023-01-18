package dev.triumphteam.cmd.core.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Pair<A, B> {

    private final A first;
    private final B second;

    public Pair(final @Nullable A first, final @Nullable B second) {
        this.first = first;
        this.second = second;
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    @Override
    public @NotNull String toString() {
        return "(" + first + "," + second + ")";
    }
}
