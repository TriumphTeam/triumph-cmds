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

import dev.triumphteam.cmd.core.extention.Result;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Splitting argument takes a string and splits it into a collection.
 *
 * @param <S> The sender type.
 */
public final class SplitStringInternalArgument<S> extends StringInternalArgument<S> {

    private final String regex;
    private final InternalArgument<S, String> internalArgument;
    private final Class<?> collectionType;

    public SplitStringInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull String regex,
            final @NotNull InternalArgument<S, String> internalArgument,
            final @NotNull Class<?> collectionType,
            final @NotNull Suggestion<S> suggestion,
            final boolean optional
    ) {
        super(name, description, String.class, suggestion, optional);
        this.regex = regex;
        this.internalArgument = internalArgument;
        this.collectionType = collectionType;
    }

    /**
     * Takes a string and splits it into a collection.
     *
     * @param sender The sender to resolve to.
     * @param value  The argument value.
     * @return A collection of the split strings.
     */
    @Override
    public @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            final @NotNull S sender,
            final @NotNull String value
    ) {
        final Stream<Object> stream = Arrays.stream(value.split(regex)).map(arg -> internalArgument.resolve(sender, arg));
        if (collectionType == Set.class) return success(stream.collect(Collectors.toSet()));
        return success(stream.collect(Collectors.toList()));
    }

    @Override
    public @NotNull List<String> suggestions(
            final @NotNull S sender,
            final @NotNull List<String> trimmed,
            final @NotNull SuggestionContext context
    ) {
        final List<String> split = Arrays.asList(trimmed.get(trimmed.size() - 1).split(regex));
        if (split.size() == 0) return Collections.emptyList();
        final String current = split.get(split.size() - 1);
        final String joined = String.join(regex, split.subList(0, split.size() - 1));
        final String map = joined.isEmpty() ? "" : joined + regex;
        return getSuggestion()
                .getSuggestions(sender, current, context)
                .stream()
                .map(it -> map + it)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull String toString() {
        return "SplitArgument{super=" + super.toString() + "}";
    }
}
