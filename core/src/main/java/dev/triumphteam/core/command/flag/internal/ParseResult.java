package dev.triumphteam.core.command.flag.internal;

import dev.triumphteam.core.command.flag.Flags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class ParseResult {

    private final List<String> leftOvers;
    private final Flags flags;

    public ParseResult(@NotNull final List<String> leftOvers, @Nullable final Flags flags) {
        this.leftOvers = leftOvers;
        this.flags = flags;
    }

    @NotNull
    public List<String> getLeftOvers() {
        return leftOvers;
    }

    @Nullable
    public Flags getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "leftOvers=" + leftOvers +
                ", flags=" + flags +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ParseResult that = (ParseResult) o;
        return Objects.equals(leftOvers, that.leftOvers) && Objects.equals(flags, that.flags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOvers, flags);
    }

}
