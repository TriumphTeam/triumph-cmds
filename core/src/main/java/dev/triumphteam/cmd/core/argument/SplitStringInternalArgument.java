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
package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final String regex,
            @NotNull final InternalArgument<S, String> internalArgument,
            @NotNull final Class<?> collectionType,
            @NotNull final Suggestion<S> suggestion,
            final int position,
            final boolean optional
    ) {
        super(name, description, String.class, suggestion, position, optional);
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
    @NotNull
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final String value) {
        final Stream<Object> stream = Arrays.stream(value.split(regex)).map(arg -> internalArgument.resolve(sender, arg));
        if (collectionType == Set.class) return stream.collect(Collectors.toSet());
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<String> suggestions(@NotNull final S sender, final @NotNull List<String> trimmed, final @NotNull SuggestionContext context) {
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
}
