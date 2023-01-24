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
package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.extention.Result;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class KeyedInternalArgument<S> extends LimitlessInternalArgument<S> {

    private final Map<Flag, StringInternalArgument<S>> flagInternalArguments;
    private final Map<Argument, StringInternalArgument<S>> argumentInternalArguments;

    private final ArgumentGroup<Argument> argumentGroup;
    private final ArgumentGroup<Flag> flagGroup;

    private final ArgumentParser argumentParser;

    public KeyedInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Map<Flag, StringInternalArgument<S>> flagInternalArguments,
            final @NotNull Map<Argument, StringInternalArgument<S>> argumentInternalArguments,
            final @NotNull ArgumentGroup<Flag> flagGroup,
            final @NotNull ArgumentGroup<Argument> argumentGroup
    ) {
        super(name, description, Flags.class, new EmptySuggestion<>(), true);
        this.flagInternalArguments = flagInternalArguments;
        this.argumentInternalArguments = argumentInternalArguments;
        this.flagGroup = flagGroup;
        this.argumentGroup = argumentGroup;
        this.argumentParser = new ArgumentParser(flagGroup, argumentGroup);
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The arguments {@link List}.
     * @return A {@link Flags} which contains the flags and leftovers.
     */
    @Override
    public @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            final @NotNull S sender,
            final @NotNull Collection<String> value
    ) {
        final ArgumentParser.Result result = argumentParser.parse(value);

        // Parsing and validating named arguments
        final Map<String, ArgumentValue> arguments = new HashMap<>();
        for (final Map.Entry<Argument, String> entry : result.getNamedArguments().entrySet()) {
            final Argument argument = entry.getKey();
            final String raw = entry.getValue();

            final StringInternalArgument<S> internalArgument = argumentInternalArguments.get(argument);
            if (internalArgument == null) continue;

            final Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolved =
                    internalArgument.resolve(sender, entry.getValue());

            if (resolved instanceof Result.Failure) {
                return resolved;
            }

            if (resolved instanceof Result.Success) {
                final Object resolvedValue = ((Result.Success<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>>) resolved).getValue();
                arguments.put(argument.getName(), new SimpleArgumentValue(raw, resolvedValue));
            }
        }

        // Parsing and validating flags
        final Map<String, ArgumentValue> flags = new HashMap<>();
        for (final Map.Entry<Flag, String> entry : result.getFlags().entrySet()) {
            final Flag flag = entry.getKey();
            final String raw = entry.getValue();

            if (!flag.hasArgument()) {
                flags.put(flag.getFlag(), EmptyArgumentValue.INSTANCE);
                flags.put(flag.getLongFlag(), EmptyArgumentValue.INSTANCE);
                continue;
            }

            final StringInternalArgument<S> internalArgument = flagInternalArguments.get(flag);
            if (internalArgument == null) continue;

            final Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolved =
                    internalArgument.resolve(sender, entry.getValue());

            if (resolved instanceof Result.Failure) {
                return resolved;
            }

            if (resolved instanceof Result.Success) {
                final Object resolvedValue = ((Result.Success<Object, BiFunction<CommandMeta, String, InvalidArgumentContext>>) resolved).getValue();

                final ArgumentValue argumentValue = new SimpleArgumentValue(raw, resolvedValue);
                flags.put(flag.getFlag(), argumentValue);
                flags.put(flag.getLongFlag(), argumentValue);
            }
        }

        return success(new KeyedArguments(arguments, flags, result.getNonTokens()));
    }

    @Override
    public @NotNull List<String> suggestions(final @NotNull S sender, final @NotNull Deque<String> arguments) {
        final String last = arguments.peekLast();
        final String current = last == null ? "" : last;

        final ArgumentParser.Result result = argumentParser.parse(arguments);
        final String resultCurrent = result.getCurrent();

        // Checking if we're waiting for a flag argument
        final List<String> waitingFlagArguments = handleFlagArgument(resultCurrent, result, sender);
        if (waitingFlagArguments != null) return waitingFlagArguments;

        // Checking if we're waiting for an argument
        final List<String> waitingArguments = handleNamedArgument(resultCurrent, result, sender);
        if (waitingArguments != null) return waitingArguments;

        // Handle flags only when they are typed
        if (current.startsWith("--")) return longFlags(resultCurrent, result.getFlags());
        if (current.startsWith("-")) return flags(resultCurrent, result.getFlags());

        // If we're not dealing with flags or arguments we return a list of named arguments that haven't been used yet
        return namedArguments(resultCurrent, result.getNamedArguments());
    }

    private @NotNull List<String> longFlags(
            final @NotNull String current,
            final @NotNull Map<Flag, String> parsed
    ) {
        return flagInternalArguments.keySet()
                .stream()
                .filter(it -> !parsed.containsKey(it))
                .map(Flag::getLongFlag)
                .filter(Objects::nonNull)
                .map(it -> "--" + it)
                .filter(it -> it.startsWith(current))
                .collect(Collectors.toList());
    }

    private @NotNull List<String> flags(
            final @NotNull String current,
            final @NotNull Map<Flag, String> parsed
    ) {
        return flagInternalArguments.keySet()
                .stream()
                .filter(it -> !parsed.containsKey(it))
                .map(Flag::getFlag)
                .filter(Objects::nonNull)
                .map(it -> "-" + it)
                .filter(it -> it.startsWith(current))
                .collect(Collectors.toList());
    }

    private @NotNull List<String> namedArguments(
            final @NotNull String current,
            final @NotNull Map<Argument, String> parsed
    ) {
        return argumentInternalArguments.keySet()
                .stream()
                .filter(it -> !parsed.containsKey(it))
                .map(Argument::getName)
                .filter(it -> it.startsWith(current))
                .map(it -> it + ":")
                .collect(Collectors.toList());
    }

    private @Nullable List<String> handleNamedArgument(
            final @NotNull String current,
            final @NotNull ArgumentParser.Result result,
            final @NotNull S sender
    ) {
        // Checking if we're waiting for an argument
        final Argument waiting = result.getArgumentWaiting();
        if (waiting == null) return null;

        // If so we get the internal version of the argument, this will likely never be null
        final InternalArgument<S, ?> internalArgument = argumentInternalArguments.get(waiting);
        if (internalArgument == null) return null;
        final String raw = waiting.getName() + ":";
        // Get suggestion from the internal argument and map it to the "raw" argument
        final List<String> suggestions = internalArgument.suggestions(
                        sender,
                        new ArrayDeque<>(Collections.singleton(current))
                ).stream()
                .map(it -> raw + it)
                .collect(Collectors.toList());

        // In case the suggestion returns nothing we just return the raw type as a suggestion
        if (suggestions.isEmpty()) return Collections.singletonList(raw);

        // If there are suggestions we return them
        return suggestions;
    }

    private @Nullable List<String> handleFlagArgument(
            final @NotNull String current,
            final @NotNull ArgumentParser.Result result,
            final @NotNull S sender
    ) {
        final Pair<Flag, ArgumentParser.Result.FlagType> waitingFlag = result.getFlagWaiting();
        if (waitingFlag == null) return null;

        final Flag flag = waitingFlag.first();
        final ArgumentParser.Result.FlagType type = waitingFlag.second();

        final InternalArgument<S, ?> internalArgument = flagInternalArguments.get(flag);
        if (internalArgument == null) return null;

        return internalArgument.suggestions(sender, new ArrayDeque<>(Collections.singleton(current)))
                .stream()
                .map(it -> {
                    if (!type.hasEquals()) return it; // No equals so we just suggest the argument
                    final String prefix = type.isLong() ? "--" + flag.getLongFlag() : "-" + flag.getFlag();
                    return prefix + "=" + it;
                }).collect(Collectors.toList());
    }
}
