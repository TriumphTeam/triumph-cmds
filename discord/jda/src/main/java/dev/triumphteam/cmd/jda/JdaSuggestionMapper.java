package dev.triumphteam.cmd.jda;

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static dev.triumphteam.cmd.jda.JdaMappingUtil.fromType;
import static dev.triumphteam.cmd.jda.JdaMappingUtil.mapChoices;

public final class JdaSuggestionMapper implements SuggestionMapper<Command.Choice> {

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
                return values.stream().filter(it -> it.getName().startsWith(input.toLowerCase())).collect(Collectors.toList());

            case CONTAINS:
                return values.stream().filter(it -> it.getName().contains(input.toLowerCase())).collect(Collectors.toList());

            default:
                return values;
        }
    }

    @Override
    public @NotNull Class<?> getType() {
        return Command.Choice.class;
    }
}
