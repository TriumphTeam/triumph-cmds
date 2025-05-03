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
package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Splitting argument takes a string and splits it into a collection.
 *
 * @param <S> The sender type.
 */
public final class SplitStringInternalArgument<S, ST> extends StringInternalArgument<S, ST> {

    private final String regex;
    private final InternalArgument<S, ST> internalArgument;
    private final Class<?> collectionType;

    public SplitStringInternalArgument(
            final @NotNull CommandMeta meta,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull String regex,
            final @NotNull InternalArgument<S, ST> internalArgument,
            final @NotNull Class<?> collectionType,
            final @NotNull InternalSuggestion<S, ST> suggestion,
            final @Nullable String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, String.class, suggestion, defaultValue, optional);
        this.regex = regex;
        this.internalArgument = internalArgument;
        this.collectionType = collectionType;
    }

    @Override
    public @NotNull InternalArgumentResult resolve(final @NotNull S sender, final @NotNull ArgumentInput input) {
        return CollectionInternalArgument.resolveCollection(sender, internalArgument, Arrays.asList(input.getInput().split(regex)), collectionType);
    }

    public @NotNull List<String> suggestions(
            final @NotNull S sender,
            final @NotNull Deque<String> arguments
    ) {
        final String peek = arguments.peekLast();
        final String last = peek == null ? "" : peek;

        final List<String> split = Arrays.asList(last.split(regex));
        if (split.size() == 0) return Collections.emptyList();

        final String current = last.endsWith(regex) ? "" : split.get(split.size() - 1);
        final String joined = String.join(regex, current.isEmpty() ? split : split.subList(0, split.size() - 1));
        final String map = joined.isEmpty() ? "" : joined + regex;

        return getSuggestion()
                .getSuggestions(sender, current, new ArrayList<>(arguments))
                .stream()
                .map(it -> map + it)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull String toString() {
        return "SplitArgument{super=" + super.toString() + "}";
    }
}
