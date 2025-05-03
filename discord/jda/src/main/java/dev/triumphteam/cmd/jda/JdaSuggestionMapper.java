package dev.triumphteam.cmd.jda;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.triumphteam.cmd.jda.JdaMappingUtil.fromType;

final class JdaSuggestionMapper implements SuggestionMapper<Command.Choice> {

    @Override
    public @NotNull List<Command.Choice> map(final @NotNull List<String> values, final @NotNull Class<?> type) {
        return mapChoices(values, fromType(type));
    }

    @Override
    public @NotNull List<String> mapBackwards(final @NotNull List<Command.Choice> values) {
        return values.stream().map(Command.Choice::getAsString).collect(Collectors.toList());
    }

    @Override
    public @NotNull List<Command.Choice> filter(final @NotNull String input, final @NotNull List<Command.Choice> values, final SuggestionMethod method) {
        switch (method) {
            case STARTS_WITH:
                return values.stream().filter(it -> it.getName().toLowerCase().startsWith(input.toLowerCase())).collect(Collectors.toList());

            case CONTAINS:
                return values.stream().filter(it -> it.getName().toLowerCase().contains(input.toLowerCase())).collect(Collectors.toList());

            default:
                return values;
        }
    }

    @Override
    public @NotNull Class<?> getType() {
        return Command.Choice.class;
    }

    @SuppressWarnings("UnstableApiUsage")
    private List<Command.Choice> mapChoices(
            final @NotNull List<String> original,
            final @NotNull OptionType type
    ) {
        final Stream<String> stream = original.stream().limit(25);

        switch (type) {
            case NUMBER:
                return stream.map(Doubles::tryParse).filter(Objects::nonNull)
                        .map(value -> new Command.Choice(value.toString(), value))
                        .collect(Collectors.toList());
            case INTEGER:
                return stream.map(Longs::tryParse).filter(Objects::nonNull)
                        .map(value -> new Command.Choice(value.toString(), value))
                        .collect(Collectors.toList());
            default:
                return stream.map(value -> new Command.Choice(value, value)).collect(Collectors.toList());
        }
    }
}
