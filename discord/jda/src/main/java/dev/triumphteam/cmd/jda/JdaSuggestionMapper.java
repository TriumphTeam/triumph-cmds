/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
