/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.slash;

import com.google.common.base.CaseFormat;
import dev.triumphteam.cmd.core.AbstractSubCommand;
import dev.triumphteam.cmd.core.argument.Argument;
import dev.triumphteam.cmd.core.argument.LimitlessArgument;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.slash.util.OptionTypeMap;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class SlashSubCommand<S> extends AbstractSubCommand<S> {

    private final String description;
    private final List<Suggestion> suggestions;

    public SlashSubCommand(
            @NotNull final SlashSubCommandProcessor<S> processor,
            @NotNull final String parentName,
            @NotNull final ExecutionProvider executionProvider
    ) {
        super(processor, parentName, executionProvider);
        this.description = processor.getDescription();
        this.suggestions = processor.getSuggestions();
    }

    public String getDescription() {
        return description;
    }

    public List<OptionData> getJdaOptions() {
        final List<OptionData> options = new ArrayList<>();
        final List<Argument<S, ?>> arguments = getArguments();

        for (int i = 0; i < arguments.size(); i++) {
            final Argument<S, ?> argument = arguments.get(i);

            final OptionData option = new OptionData(
                    OptionTypeMap.fromType(argument.getType()),
                    CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, argument.getName()),
                    argument.getDescription(),
                    !argument.isOptional()
            );

            options.add(option);

            final Suggestion suggestion = getSuggestion(i);
            if (suggestion instanceof EmptySuggestion) continue;

            option.addChoices(suggestion.getSuggestions().stream().map(it -> new Command.Choice(it, it)).limit(25).collect(Collectors.toList()));

            // TODO: 11/28/2021 This is a hack, but it works for now.
            if (argument instanceof LimitlessArgument) break;
        }

        return options;
    }

    @NotNull
    private Suggestion getSuggestion(final int index) {
        if (index >= suggestions.size()) return EmptySuggestion.INSTANCE;
        return suggestions.get(index);
    }

}
