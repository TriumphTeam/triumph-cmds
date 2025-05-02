package dev.triumphteam.cmd.core.extension;

import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SuggestionMapper<ST> {

    @NotNull List<ST> map(final @NotNull List<String> values, final @NotNull Class<?> type);

    default @NotNull List<ST> map(final @NotNull List<String> values) {
        return map(values, String.class);
    }

    @NotNull List<String> mapBackwards(final @NotNull List<ST> values);

    @NotNull List<ST> filter(final @NotNull String input, final @NotNull List<ST> values, final SuggestionMethod method);

    @NotNull Class<?> getType();
}
