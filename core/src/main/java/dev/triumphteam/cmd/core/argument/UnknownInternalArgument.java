package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.extention.Result;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * This argument type is always ignored by the creator.
 * This is only used for arguments that are meant to be hidden and not actually part of a command.
 */
public final class UnknownInternalArgument<S> extends StringInternalArgument<S> {

    public UnknownInternalArgument(final @NotNull Class<?> type) {
        super("unknown", "unknown.", type, new EmptySuggestion<>(), false);
    }

    @Override
    public @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(final @NotNull S sender, final @NotNull String value) {
        return null;
    }

    @Override
    public @NotNull List<String> suggestions(final @NotNull S sender, final @NotNull List<String> trimmed, final @NotNull SuggestionContext context) {
        return Collections.emptyList();
    }

    @Override
    public @NotNull String toString() {
        return "UnknownInternalArgument{} " + super.toString();
    }
}
