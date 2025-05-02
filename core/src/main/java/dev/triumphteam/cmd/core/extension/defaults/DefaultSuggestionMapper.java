package dev.triumphteam.cmd.core.extension.defaults;

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public final class DefaultSuggestionMapper implements SuggestionMapper<String> {

    @Override
    public @NotNull List<String> map(final @NotNull List<String> values, final @NotNull Class<?> type) {
        return values;
    }

    @Override
    public @NotNull List<String> mapBackwards(final @NotNull List<String> values) {
        return values;
    }

    @Override
    public @NotNull List<String> filter(final @NotNull String input, final @NotNull List<String> values, final SuggestionMethod method) {
        switch (method) {
            case STARTS_WITH:
                return values.stream().filter(it -> it.toLowerCase().startsWith(input.toLowerCase())).collect(Collectors.toList());

            case CONTAINS:
                return values.stream().filter(it -> it.toLowerCase().contains(input.toLowerCase())).collect(Collectors.toList());

            default:
                return values;
        }
    }

    @Override
    public @NotNull Class<?> getType() {
        return String.class;
    }
}
