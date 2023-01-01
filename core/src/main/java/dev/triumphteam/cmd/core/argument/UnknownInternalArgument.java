package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * This argument type is always ignored by the creator.
 * This is only used for arguments that are meant to be hidden and not actually part of a command.
 */
public final class UnknownInternalArgument<S> implements InternalArgument<S, String> {

    private final Class<?> type;

    public UnknownInternalArgument(final @NotNull Class<?> type) {
        this.type = type;
    }

    @Override
    public @NotNull String getName() {
        return "unknown";
    }

    @Override
    public @NotNull String getDescription() {
        return "Unknown.";
    }

    @Override
    public @NotNull Class<?> getType() {
        return type;
    }

    @Override
    public boolean isOptional() {
        return false;
    }

    @Override
    public @Nullable Object resolve(final @NotNull S sender, final @NotNull String value) {
        return null;
    }

    @Override
    public @NotNull List<String> suggestions(final @NotNull S sender, final @NotNull List<String> trimmed, final @NotNull SuggestionContext context) {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "UnknownInternalArgument{" +
                "type=" + type +
                '}';
    }
}
